package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

public class NitroRemover extends NitroListener implements MessageCreateListener {

  public NitroRemover(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    if (event.getChannel().getIdAsString().equals(config.getMainChannel())) {
      if (event.getMessage().getContent().equalsIgnoreCase("!nitro-remove")) {
        event
            .getMessageAuthor()
            .asUser()
            .ifPresent(
                user -> {
                  if (isNitro(user) && !isBanned(user)) {
                    String discordId = event.getMessageAuthor().getIdAsString();
                    if (config.getUser(discordId).isPresent()) {
                      NitroUser nitro = config.getUser(discordId).get();
                      NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitro));
                      new MessageBuilder()
                          .append(
                              ":white_check_mark: "
                                  + user.getMentionTag()
                                  + " You have removed Nitro Boosting privileges from `"
                                  + nitro.getMinecraftUsername()
                                  + "` (`"
                                  + nitro.getPlayerId().toString()
                                  + "`). You may use `!nitro-redeem <minecraft username>` to redeem them again.")
                          .send(event.getChannel());
                    } else {
                      new MessageBuilder()
                          .append(
                              ":negative_squared_cross_mark: "
                                  + user.getMentionTag()
                                  + " You have not yet redeemed your Nitro Boosting privileges. Use `!nitro-redeem <minecraft username>` to claim them. For more information, use `!nitro-help`.")
                          .send(event.getChannel());
                    }
                  } else {
                    new MessageBuilder()
                        .append(
                            ":negative_squared_cross_mark: "
                                + user.getMentionTag()
                                + " You are not allowed to use this command! If you believe this is a mistake, contact a staff member.")
                        .send(event.getChannel());
                  }
                });
      }
    } else if (event.getChannel().getIdAsString().equals(config.getStaffChannel())) {
      if (event.getMessage().getContent().startsWith("!nitro-force-remove")) {
        if (event.getMessage().getContent().split(" ").length == 2) {
          String discordId = event.getMessage().getContent().split(" ")[1];
          if (config.getUser(discordId).isPresent()) {
            NitroUser nitro = config.getUser(discordId).get();
            NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitro));
            new MessageBuilder()
                .append(
                    ":white_check_mark: "
                        + event.getMessageAuthor().asUser().get().getMentionTag()
                        + " You have forcefully removed Nitro Boosting privileges from `"
                        + nitro.getMinecraftUsername()
                        + "`. Originally claimed by `"
                        + nitro.getDiscriminatedUsername()
                        + "` (`"
                        + nitro.getPlayerId().toString()
                        + "`).")
                .send(event.getChannel());
            api.alert(
                ":triangular_flag_on_post: `"
                    + event.getMessageAuthor().getDiscriminatedName()
                    + "` (`"
                    + event.getMessageAuthor().getIdAsString()
                    + "`) has forcefully removed Nitro Boosting privileges from `"
                    + nitro.getMinecraftUsername()
                    + "` Originally claimed by `"
                    + nitro.getDiscriminatedUsername()
                    + "` (`"
                    + discordId
                    + "`).");
          } else {
            new MessageBuilder()
                .append(
                    ":negative_squared_cross_mark: "
                        + event.getMessageAuthor().asUser().get().getMentionTag()
                        + " No active Nitro Booster with the Discord ID `"
                        + discordId
                        + "` was found in the configuration. Use `!nitro-list boosters` for a list of active boosters.")
                .send(event.getChannel());
          }
        } else {
          new MessageBuilder()
              .append(
                  ":warning: Incorrect syntax! Use `!nitro-remove force <discord id>` to remove a user's Nitro Boosting privileges.")
              .send(event.getChannel());
        }
      }
    }
  }
}
