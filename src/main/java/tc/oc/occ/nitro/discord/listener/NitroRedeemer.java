package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.entity.message.MessageBuilder;
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
      if (event.getMessage().getContent().startsWith("!nitro")) {
        event
            .getMessageAuthor()
            .asUser()
            .ifPresent(
                user -> {
                  if (isNitro(user)) {
                    String[] parts = event.getMessage().getContent().split(" ");

                    if (parts.length == 2) {
                      String discriminatedUsername =
                          event.getMessageAuthor().getDiscriminatedName();
                      String discordId = event.getMessageAuthor().getIdAsString();
                      String username = parts[1].replace("@", "").trim();

                      if (config.getUser(discordId).isPresent()) {
                        NitroUser nitro = config.getUser(discordId).get();
                        new MessageBuilder()
                            .append(
                                ":negative_squared_cross_mark: "
                                    + user.getMentionTag()
                                    + " Your Nitro Boosting privileges have already been claimed for `"
                                    + nitro.getMinecraftUsername()
                                    + "` (`"
                                    + nitro.getPlayerId().toString()
                                    + "`).")
                            .send(event.getChannel());
                      } else {
                        WebUtils.getUUID(username)
                            .thenAcceptAsync(
                                uuid -> {
                                  if (uuid != null) {
                                    NitroUser nitro =
                                        config.addNitro(
                                            discriminatedUsername, discordId, username, uuid);
                                    NitroCloudy.get().callSyncEvent(new NitroUserAddEvent(nitro));
                                    new MessageBuilder()
                                        .append(
                                            ":white_check_mark: "
                                                + user.getMentionTag()
                                                + " Your Nitro Boosting privileges have been claimed for `"
                                                + nitro.getMinecraftUsername()
                                                + "` (`"
                                                + nitro.getPlayerId().toString()
                                                + "`). Thanks for boosting the server!")
                                        .send(event.getChannel());
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
                      new MessageBuilder()
                          .append(":warning: Incorrect syntax! Please use `!nitro <name>`")
                          .send(event.getChannel());
                    }
                  } else {
                    new MessageBuilder()
                        .append(
                            ":negative_squared_cross_mark: "
                                + user.getMentionTag()
                                + " Only Nitro Boosters can use this command! Support us by boosting the server!")
                        .send(event.getChannel());
                  }
                });
      }
    }
  }
}
