package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.data.BannedNitroUser;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

public class NitroBan extends NitroListener implements MessageCreateListener {

  public NitroBan(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    if (event.getChannel().getIdAsString().equals(config.getStaffChannel())) {
      if (event.getMessage().getContent().startsWith("!nitro-ban")) {
        if (event.getMessage().getContent().split(" ").length == 3) {
          String discriminatedUsername = event.getMessage().getContent().split(" ")[1];
          String discordId = event.getMessage().getContent().split(" ")[2];
          if (discordId.length() == 18) {
            if (config.getBannedUsers().stream()
                .noneMatch(user -> user.getDiscordId().equals(discordId))) {
              new MessageBuilder()
                  .append(
                      ":hammer: "
                          + event.getMessageAuthor().asUser().get().getMentionTag()
                          + " The user `"
                          + discriminatedUsername
                          + "` with Discord ID `"
                          + discordId
                          + "` has been banned from redeeming Nitro.")
                  .send(event.getChannel());
              api.alert(
                  ":hammer: `"
                      + event.getMessageAuthor().getDiscriminatedName()
                      + "` (`"
                      + event.getMessageAuthor().getIdAsString()
                      + "`) has banned `"
                      + discriminatedUsername
                      + "` with Discord ID `"
                      + discordId
                      + "` from redeeming Nitro.");
              config.getBannedUsers().add(new BannedNitroUser(discriminatedUsername, discordId));
              // If the user was an active booster, remove him from the nitro-boosters list.
              if (config.getUsers().stream()
                  .anyMatch(user -> user.getDiscordId().equals(discordId))) {
                NitroUser nitro = config.getUser(discordId).get();
                NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitro));
              }
            } else {
              new MessageBuilder()
                  .append(
                      ":negative_squared_cross_mark: "
                          + event.getMessageAuthor().asUser().get().getMentionTag()
                          + " The user `"
                          + discriminatedUsername
                          + "` with Discord ID `"
                          + discordId
                          + "` is already banned. Use `!nitro-list bans` to see the list of banned users.")
                  .send(event.getChannel());
            }
          } else {
            new MessageBuilder()
                .append(
                    ":negative_squared_cross_mark: "
                        + event.getMessageAuthor().asUser().get().getMentionTag()
                        + " The Discord ID `"
                        + discordId
                        + "` is not a valid 18-digit ID.")
                .send(event.getChannel());
          }
        } else {
          new MessageBuilder()
              .append(
                  ":warning: Incorrect syntax! Please use `!nitro-ban <discriminated user> <discord id>`")
              .send(event.getChannel());
        }
      } else if (event.getMessage().getContent().startsWith("!nitro-unban")) {
        if (event.getMessage().getContent().split(" ").length == 3) {
          String discriminatedUsername = event.getMessage().getContent().split(" ")[1];
          String discordId = event.getMessage().getContent().split(" ")[2];
          if (discordId.length() == 18) {
            if (config.getBannedUsers().stream()
                .anyMatch(user -> user.getDiscordId().equals(discordId))) {
              new MessageBuilder()
                  .append(
                      ":white_check_mark: "
                          + event.getMessageAuthor().asUser().get().getMentionTag()
                          + " The user `"
                          + discriminatedUsername
                          + "` with Discord ID `"
                          + discordId
                          + "` has been unbanned. They may now redeem Nitro.")
                  .send(event.getChannel());
              api.alert(
                  ":flag_white: `"
                      + event.getMessageAuthor().getDiscriminatedName()
                      + "` (`"
                      + event.getMessageAuthor().getIdAsString()
                      + "`) has unbanned `"
                      + discriminatedUsername
                      + "` with Discord ID `"
                      + discordId
                      + "` from redeeming Nitro.");
              config.getBannedUsers().removeIf(user -> user.getDiscordId().equals(discordId));
            } else {
              new MessageBuilder()
                  .append(
                      ":negative_squared_cross_mark: "
                          + event.getMessageAuthor().asUser().get().getMentionTag()
                          + " The user with Discord ID `"
                          + discordId
                          + "` is not currently banned. Use `!nitro-list bans` to see the list of banned users.")
                  .send(event.getChannel());
            }
          } else {
            new MessageBuilder()
                .append(
                    ":negative_squared_cross_mark: "
                        + event.getMessageAuthor().asUser().get().getMentionTag()
                        + " The Discord ID `"
                        + discordId
                        + "` is not a valid 18-digit ID. Please try again.")
                .send(event.getChannel());
          }
        } else {
          new MessageBuilder()
              .append(
                  ":warning: Incorrect syntax! Please use `!nitro-unban <discriminated user> <discord id>`")
              .send(event.getChannel());
        }
      }
    }
  }
}
