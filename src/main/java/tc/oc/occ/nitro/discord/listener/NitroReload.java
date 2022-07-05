package tc.oc.occ.nitro.discord.listener;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroReload extends NitroListener implements MessageCreateListener {

  public NitroReload(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    if (event.getChannel().getIdAsString().equals(config.getStaffChannel())) {
      if (event.getMessage().getContent().startsWith("!nitro-reload")) {
        new MessageBuilder()
            .append(
                ":white_check_mark: "
                    + event.getMessageAuthor().asUser().get().getMentionTag()
                    + " The configuration has been reloaded!")
            .send(event.getChannel());
        api.alert(
            ":arrows_clockwise: `"
                + event.getMessageAuthor().getDiscriminatedName()
                + "` (`"
                + event.getMessageAuthor().getIdAsString()
                + "`) has reloaded the configuration.");
        NitroCloudy.get().reloadBotConfig();
      }
    }
  }
}
