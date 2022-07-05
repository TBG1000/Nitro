package tc.oc.occ.nitro.discord.listener;

import java.util.stream.Collectors;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroList extends NitroListener implements MessageCreateListener {

  public NitroList(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    EmbedBuilder boostersList =
        new EmbedBuilder()
            .setTitle("Nitro Boosters")
            .setDescription("List of active Nitro boosters found in the configuration file.")
            .addField("Format", "`discriminated user:discord id:minecraft username`")
            .setFooter(
                "Requested by " + event.getMessageAuthor().getDiscriminatedName(),
                event.getMessageAuthor().getAvatar());

    EmbedBuilder bansList =
        new EmbedBuilder()
            .setTitle("Nitro banned users")
            .setDescription("List of banned users found in the configuration file.")
            .addField("Format", "`discriminated user:discord id`")
            .setFooter(
                "Requested by " + event.getMessageAuthor().getDiscriminatedName(),
                event.getMessageAuthor().getAvatar());

    EmbedBuilder commandsList =
        new EmbedBuilder()
            .setTitle("Nitro redemption/removal commands")
            .setDescription("List of redemption/removal commands found in the configuration file.")
            .setFooter(
                "Requested by " + event.getMessageAuthor().getDiscriminatedName(),
                event.getMessageAuthor().getAvatar());

    if (event.getChannel().getIdAsString().equals(config.getStaffChannel())) {
      if (event.getMessage().getContent().startsWith("!nitro-list")) {
        String[] parts = event.getMessage().getContent().split(" ");
        if (parts.length == 2) {
          if (parts[1].equalsIgnoreCase("boosters")) {
            if (config.getUsers().isEmpty()) {
              boostersList.addField("Boosters", "_No nitro boosters found_", false);
            } else {
              boostersList.addField(
                  "Boosters",
                  config.getUsers().stream()
                      .map(
                          user ->
                              "`- "
                                  + user.getDiscriminatedUsername()
                                  + ":"
                                  + user.getDiscordId()
                                  + ":"
                                  + user.getMinecraftUsername()
                                  + "`")
                      .collect(Collectors.joining("\n")));
            }
            event.getChannel().sendMessage(boostersList);
          } else if (parts[1].equalsIgnoreCase("bans")) {
            if (config.getBannedUsers().isEmpty()) {
              bansList.addField("Banned users", "_No banned users found_");
            } else {
              bansList.addField(
                  "Banned users",
                  config.getBannedUsers().stream()
                      .map(
                          user ->
                              "`- "
                                  + user.getDiscriminatedUsername()
                                  + ":"
                                  + user.getDiscordId()
                                  + "`")
                      .collect(Collectors.joining("\n")));
            }
            event.getChannel().sendMessage(bansList);
          } else if (parts[1].equalsIgnoreCase("commands")) {
            if (config.getRedemptionCommands().isEmpty()) {
              commandsList.addField("Redemption commands", "_No redemption commands found_");
            } else {
              commandsList.addField(
                  "Redemption commands",
                  config.getRedemptionCommands().stream()
                      .map(command -> "`- " + command + "`")
                      .collect(Collectors.joining("\n")));
            }
            if (config.getRemovalCommands().isEmpty()) {
              commandsList.addField("Removal commands", "_No removal commands found_");
            } else {
              commandsList.addField(
                  "Removal commands",
                  config.getRemovalCommands().stream()
                      .map(command -> "`- " + command + "`")
                      .collect(Collectors.joining("\n")));
            }
            event.getChannel().sendMessage(commandsList);
          } else {
            new MessageBuilder()
                .append(
                    ":warning: List not found! Please use `!nitro-list <boosters|bans|commands>`")
                .send(event.getChannel());
          }
        } else {
          new MessageBuilder()
              .append(
                  ":warning: Incorrect syntax! Please use `!nitro-list <boosters|bans|commands>`")
              .send(event.getChannel());
        }
      }
    }
  }
}
