package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NitroList extends NitroListener  {
  public NitroList(DiscordBot api, NitroConfig config) { super(api, config);}

    private final String[] listTypes = new String[]{"boosters", "commands"};

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder boostersList = new EmbedBuilder()
                .setTitle("Nitro Boosters")
                .setDescription("List of active Nitro boosters found in the configuration file.")
                .addField("Format", "`discord username:discord id:minecraft username`", false);
        EmbedBuilder commandsList = new EmbedBuilder()
                .setTitle("Nitro redemption/removal commands")
                .setDescription("List of redemption/removal commands found in the configuration file.");
        if (event.getName().equals("list")) {
            if (!event.getChannelId().equals(config.getStaffChannel())) {
                event.reply(":warning: Run this command in staff channel!").setEphemeral(true).queue();
            }
            OptionMapping optionMapping = event.getOption("list");
            if (optionMapping == null) return;
            String messageInput = optionMapping.getAsString();

            switch (messageInput.toLowerCase()) {
                case "boosters":
                    boostersList.addField("Boosters", config.getUsers().stream().map(user -> "`- " + user.getDiscordUsername() + ":" + user.getDiscordId() + ":" + user.getMinecraftUsername() + "`").collect(Collectors.joining("\n")), false);
                    event.replyEmbeds(boostersList.build()).queue();
                case "commands":
                    if (config.getRedemptionCommands().isEmpty()) {
                        commandsList.addField("Redemption commands", "_No redemption commands found._", false);
                    } else {
                        commandsList.addField(
                                "Redemption commands",
                                config.getRedemptionCommands().stream()
                                        .map(command -> "`- " + command + "`")
                                        .collect(Collectors.joining("\n")), false);
                    }
                    if (config.getRemovalCommands().isEmpty()) {
                        commandsList.addField("Removal commands", "_No removal commands found._", false);
                    } else {
                        commandsList.addField(
                                "Removal commands",
                                config.getRemovalCommands().stream()
                                        .map(command -> "`- " + command + "`")
                                        .collect(Collectors.joining("\n")), false);
                    }
                    event.replyEmbeds(commandsList.build()).queue();
                default:
                    event.reply(":no_entry_sign: Invalid list provided! Options are boosters or commands.").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
      if (event.getName().equals("list") && event.getFocusedOption().getName().equals("list")) {
          List<Command.Choice> options = Stream.of(listTypes)
                  .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                  .map(word -> new Command.Choice(word, word))
                  .collect(Collectors.toList());
          event.replyChoices(options).queue();
      }
    }
}
