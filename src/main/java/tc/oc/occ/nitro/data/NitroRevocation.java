package tc.oc.occ.nitro.data;

import java.time.Instant;

public class NitroRevocation {

  private final String discordUsername;
  private final String discordId;
  private final Long expiresAt;

  public NitroRevocation(String discordUsername, String discordId, Long expiresAt) {
    this.discordUsername = discordUsername;
    this.discordId = discordId;
    this.expiresAt = expiresAt;
  }

  public static NitroRevocation of(String data) {
    String[] parts = data.split(":", 3);
    if (parts.length < 2) {
      return null;
    }
    String username = parts[0];
    String id = parts[1];
    Long expiresAt = null;
    if (parts.length == 3 && parts[2] != null && !parts[2].isEmpty()) {
      if (!parts[2].equalsIgnoreCase("indefinite")) {
        try {
          expiresAt = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
          return null;
        }
      }
    }
    return new NitroRevocation(username, id, expiresAt);
  }

  public String getDiscordUsername() {
    return discordUsername;
  }

  public String getDiscordId() {
    return discordId;
  }

  public Long getExpiresAt() {
    return expiresAt;
  }

  public boolean isExpired(Instant now) {
    return expiresAt != null && now.toEpochMilli() >= expiresAt;
  }

  public String toString() {
    return discordUsername + ":" + discordId + ":" + (expiresAt == null ? "indefinite" : expiresAt);
  }
}
