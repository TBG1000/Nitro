package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.WebUtils;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserAddEvent;

public class NitroRedeemer extends NitroListener implements MessageCreateListener {

  public NitroRedeemer(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    if (event.getChannel().getIdAsString().equals(config.getMainChannel())) {
      if (event.getMessage().getContent().startsWith("!nitro-redeem")) {
        api.deleteCommand(event);
        event
            .getMessageAuthor()
            .asUser()
            .ifPresent(
                user -> {
                  if (isNitro(user) && !isBanned(user)) {
                    String[] parts = event.getMessage().getContent().split(" ");
                    if (parts.length == 2) {
                      String discriminatedUsername =
                          event.getMessageAuthor().getDiscriminatedName();
                      String discordId = event.getMessageAuthor().getIdAsString();
                      String username = parts[1].replace("@", "").trim();

                      if (config.getUser(discordId).isPresent()) {
                        NitroUser nitro = config.getUser(discordId).get();
                        api.sendMessage(
                            ":negative_squared_cross_mark: "
                                + user.getMentionTag()
                                + " Your Nitro Boosting privileges have already been claimed for `"
                                + nitro.getMinecraftUsername()
                                + "` (`"
                                + nitro.getPlayerId().toString()
                                + "`). If you wish to change this, use `!nitro-remove` or contact a staff member.",
                            false);
                      } else {
                        WebUtils.getUUID(username)
                            .thenAcceptAsync(
                                uuid -> {
                                  if (uuid != null) {
                                    NitroUser nitro =
                                        config.addNitro(
                                            discriminatedUsername, discordId, username, uuid);
                                    NitroCloudy.get().callSyncEvent(new NitroUserAddEvent(nitro));
                                    api.sendMessage(
                                        ":white_check_mark: "
                                            + user.getMentionTag()
                                            + " Your Nitro Boosting privileges have been claimed for `"
                                            + nitro.getMinecraftUsername()
                                            + "` (`"
                                            + nitro.getPlayerId().toString()
                                            + "`). If something went wrong, or you're missing in-game perks, contact a staff member. Thanks for boosting the server!",
                                        false);
                                  } else {
                                    api.alert(
                                        ":warning: Unable to find UUID for user "
                                            + discordId
                                            + " - "
                                            + username);
                                  }
                                });
                      }

                    } else {
                      api.sendMessage(
                          ":warning: Incorrect syntax! Please use `!nitro-redeem <minecraft username>`. For more information, use `!nitro-help`.",
                          false);
                    }
                  } else {
                    api.sendMessage(
                        ":negative_squared_cross_mark: "
                            + user.getMentionTag()
                            + " You are not allowed to use this command! If you believe this is a mistake, contact a staff member.",
                        false);
                  }
                });
      }
    }
  }
}
