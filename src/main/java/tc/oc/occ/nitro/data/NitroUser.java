package tc.oc.occ.nitro.data;

import java.util.UUID;

/** NitroUser - A linked player who has nitro * */
public class NitroUser {

  private String discordId;
  private UUID playerId;
  private String discriminatedUsername;
  private String minecraftUsername;

  public NitroUser(
      String discriminatedUsername, String discordId, String minecraftUsername, UUID playerId) {
    this.discriminatedUsername = discriminatedUsername;
    this.discordId = discordId;
    this.minecraftUsername = minecraftUsername;
    this.playerId = playerId;
  }

  public String getDiscordId() {
    return discordId;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public String getDiscriminatedUsername() {
    return discriminatedUsername;
  }

  public String getMinecraftUsername() {
    return minecraftUsername;
  }

  @Override
  public String toString() {
    return String.format(
        "%s:%s:%s:%s", discriminatedUsername, discordId, minecraftUsername, playerId.toString());
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof NitroUser)
        && ((NitroUser) other).getDiscordId().equalsIgnoreCase(getDiscordId());
  }

  public static NitroUser of(String data) {
    String[] parts = data.split(":");
    if (parts.length == 4) {
      String discordName = parts[0];
      String discordId = parts[1];
      String minecraftName = parts[2];
      String uuidStr = parts[3];
      return new NitroUser(discordName, discordId, minecraftName, UUID.fromString(uuidStr));
    }
    return null;
  }
}
