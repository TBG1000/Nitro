package tc.oc.occ.nitro.discord.listener;

import java.util.concurrent.TimeUnit;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroHelp extends NitroListener implements MessageCreateListener {

  public NitroHelp(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    if (event.getMessage().getContent().equalsIgnoreCase("!nitro-help")) {
      api.deleteCommand(event);
      EmbedBuilder helpEmbed =
          new EmbedBuilder()
              .setTitle("Nitro Commands")
              .setDescription(
                  "Available commands for the Nitro bot. View the source code [here](https://github.com/TBG1000/Nitro).")
              .addField(
                  "Boosters",
                  "`!nitro-redeem <minecraft username>` - Redeem Nitro privileges\n"
                      + "`!nitro-remove` - Removes Nitro privileges\n"
                      + "`!nitro-help` - Display this menu")
              .addField(
                  "Staff",
                  "`!nitro-list <boosters|bans|commands>`\n"
                      + "`!nitro-force-remove <discord id>` - Forcefully remove a user's Nitro privileges\n"
                      + "`!nitro-ban <discriminated username> <discord id>` - Ban a user from redeeming Nitro privileges\n"
                      + "`!nitro-unban <discriminated username> <discord id>` - Unban a user from redeeming Nitro privileges\n"
                      + "`!nitro-reload` - Reload the configuration file\n\n"
                      + "_Note: staff commands may only be used in the configured staff channel_")
              .setFooter(
                  "Requested by " + event.getMessageAuthor().getDiscriminatedName(),
                  event.getMessageAuthor().getAvatar());
      event
          .getChannel()
          .sendMessage(helpEmbed)
          .thenAccept(
              embed ->
                  embed
                      .getApi()
                      .getThreadPool()
                      .getScheduler()
                      .schedule(() -> embed.delete(), 30, TimeUnit.SECONDS));
    }
  }
}
