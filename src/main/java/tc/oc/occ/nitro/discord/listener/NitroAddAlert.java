package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.listener.server.role.UserRoleAddListener;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroAddAlert extends NitroListener implements UserRoleAddListener {

  public NitroAddAlert(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onUserRoleAdd(UserRoleAddEvent event) {
    if (isNitro(event.getRole())) {
      // ADD ROLE
      api.sendMessage(
          ":100: Thanks for boosting the server "
              + event.getUser().getMentionTag()
              + "! Use `!nitro <minecraft username>` to claim your in-game privileges :tada:",
          false);
    }
  }
}
