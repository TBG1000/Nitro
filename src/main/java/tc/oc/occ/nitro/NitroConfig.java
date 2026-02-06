package tc.oc.occ.nitro;

import org.bukkit.configuration.Configuration;
import tc.oc.occ.nitro.data.NitroRevocation;
import tc.oc.occ.nitro.data.NitroUser;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class NitroConfig {

  private boolean enabled;

  private String token;
  private String server;
  private String nitroRole;

  private List<NitroUser> nitroUsers;
  private List<NitroRevocation> revokedUsers;

  private String alertChannel;
  private String mainChannel;
  private String staffChannel;

  private List<String> redemptionCommands;
  private List<String> removalCommands;

  public NitroConfig(Configuration config) {
    reload(config);
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean("enabled");
    this.token = config.getString("token");
    this.server = config.getString("server");
    this.nitroRole = config.getString("nitro-role");
    this.alertChannel = config.getString("channel-alerts");
    this.mainChannel = config.getString("channel-main");
    this.staffChannel = config.getString("channel-staff");
    this.redemptionCommands = config.getStringList("redemption-commands");
    this.removalCommands = config.getStringList("removal-commands");

    List<String> nitroData = config.getStringList("nitro-boosters");
    this.nitroUsers =
        nitroData.stream()
            .filter(str -> str != null && !str.isEmpty())
            .map(NitroUser::of)
            .collect(Collectors.toList());
    List<String> revokedData = config.getStringList("revoked-users");
    this.revokedUsers =
        revokedData.stream()
            .filter(str -> str != null && !str.isEmpty())
            .map(NitroRevocation::of)
            .filter(revocation -> revocation != null)
            .filter(revocation -> !revocation.isExpired(Instant.now()))
            .collect(Collectors.toList());
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getToken() {
    return token;
  }

  public String getServer() {
    return server;
  }

  public String getNitroRole() {
    return nitroRole;
  }

  public String getAlertChannel() {
    return alertChannel;
  }

  public String getMainChannel() {
    return mainChannel;
  }

  public String getStaffChannel() {
    return staffChannel;
  }

  public List<NitroUser> getUsers() {
    return nitroUsers;
  }

  public List<NitroRevocation> getRevokedUsers() {
    return revokedUsers;
  }

  public List<String> getRedemptionCommands() {
    return redemptionCommands;
  }

  public List<String> getRemovalCommands() {
    return removalCommands;
  }

  public NitroUser addNitro(
    String discordUsername, String discordId, String minecraftUsername, UUID playerId) {
    NitroUser user = new NitroUser(discordUsername, discordId, minecraftUsername, playerId);
    this.nitroUsers.add(user);
    return user;
  }

  public Optional<NitroUser> getUser(String discordId) {
    return nitroUsers.stream()
        .filter(user -> user.getDiscordId().equalsIgnoreCase(discordId))
        .findAny();
  }

  public Optional<NitroRevocation> getRevocation(String discordId) {
    Optional<NitroRevocation> revocation =
        revokedUsers.stream()
            .filter(user -> user.getDiscordId().equalsIgnoreCase(discordId))
            .findAny();
    if (revocation.isPresent() && revocation.get().isExpired(Instant.now())) {
      revokedUsers.remove(revocation.get());
      return Optional.empty();
    }
    return revocation;
  }

  public NitroRevocation addRevocation(
      String discordUsername, String discordId, Long expiresAt) {
    NitroRevocation revocation = new NitroRevocation(discordUsername, discordId, expiresAt);
    revokedUsers.removeIf(entry -> entry.getDiscordId().equalsIgnoreCase(discordId));
    revokedUsers.add(revocation);
    return revocation;
  }

  public boolean removeExpiredRevocations() {
    int size = revokedUsers.size();
    revokedUsers.removeIf(revocation -> revocation.isExpired(Instant.now()));
    return size != revokedUsers.size();
  }

  public void removeNitro(NitroUser user) {
    nitroUsers.remove(user);
  }

  public void save(Configuration config) {
    config.set(
        "nitro-boosters",
        nitroUsers.stream().map(NitroUser::toString).collect(Collectors.toList()));
    config.set(
        "revoked-users",
        revokedUsers.stream().map(NitroRevocation::toString).collect(Collectors.toList()));
  }
}
