package tc.oc.occ.nitro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.configuration.Configuration;
import tc.oc.occ.nitro.data.BannedNitroUser;
import tc.oc.occ.nitro.data.NitroUser;

public class NitroConfig {

  private boolean enabled;

  private String token;
  private String server;
  private String nitroRole;

  private List<NitroUser> nitroUsers;
  private List<BannedNitroUser> bannedUsers;

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

    List<String> bannedData = config.getStringList("banned-boosters");
    this.bannedUsers =
        bannedData.stream()
            .filter(str -> str != null && !str.isEmpty())
            .map(BannedNitroUser::of)
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

  public List<BannedNitroUser> getBannedUsers() {
    return bannedUsers;
  }

  public List<String> getRedemptionCommands() {
    return redemptionCommands;
  }

  public List<String> getRemovalCommands() {
    return removalCommands;
  }

  public NitroUser addNitro(
      String discriminatedUsername, String discordId, String minecraftUsername, UUID playerId) {
    NitroUser user = new NitroUser(discriminatedUsername, discordId, minecraftUsername, playerId);
    this.nitroUsers.add(user);
    return user;
  }

  public Optional<NitroUser> getUser(String discordId) {
    return nitroUsers.stream()
        .filter(user -> user.getDiscordId().equalsIgnoreCase(discordId))
        .findAny();
  }

  public void removeNitro(NitroUser user) {
    nitroUsers.remove(user);
  }

  public void save(Configuration config) {
    config.set(
        "nitro-boosters",
        nitroUsers.stream().map(NitroUser::toString).collect(Collectors.toList()));
  }
}
