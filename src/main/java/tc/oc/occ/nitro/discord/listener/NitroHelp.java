package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroHelp extends NitroListener {

  public NitroHelp(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    MessageEmbed helpEmbed = new EmbedBuilder()
            .setTitle("Nitro Commands")
            .setDescription(
                    "Available commands for NitroBot. View the source code [here](https://github.com/TBG1000/Nitro).")
            .addField(
                    "Boosters",
                    "`/redeem <Minecraft username>` - Redeem Nitro privileges\n"
                            + "`/remove` - Removes Nitro privileges\n"
                            + "`/help` - Display this menu", false)
            .addField(
                    "Staff",
                    "`/list <boosters|commands>`\n"
                            + "`/revoke <Discord user> [duration]` - Revoke Nitro privileges (e.g. `2h`, `3d`, `1w`)\n"
                            + "`/config-reload` - Reload the configuration file\n\n"
                            + "_Note: staff commands may only be used in the configured staff channel._", false).build();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("help")) {
            event.replyEmbeds(helpEmbed).setEphemeral(true).queue();
        }
    }
}
