package tc.oc.occ.nitro.data;

/** NitroUser - A linked player who has nitro * */
public class BannedNitroUser {

  private String discriminatedUsername;
  private String discordId;

  public BannedNitroUser(String discriminatedUsername, String discordId) {
    this.discriminatedUsername = discriminatedUsername;
    this.discordId = discordId;
  }

  public String getDiscriminatedUsername() {
    return discriminatedUsername;
  }

  public String getDiscordId() {
    return discordId;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", discriminatedUsername, discordId);
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof BannedNitroUser)
        && ((BannedNitroUser) other).getDiscordId().equalsIgnoreCase(getDiscordId());
  }

  public static BannedNitroUser of(String data) {
    String[] parts = data.split(":");
    if (parts.length == 2) {
      String discordName = parts[0];
      String discordId = parts[1];
      return new BannedNitroUser(discordName, discordId);
    }
    return null;
  }
}
