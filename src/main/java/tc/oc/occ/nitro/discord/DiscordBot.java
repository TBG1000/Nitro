package tc.oc.occ.nitro.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.listener.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DiscordBot {

  private JDA api;

  private final NitroConfig config;
  private final Logger logger;

  public DiscordBot(NitroConfig config, Logger logger) {
    this.config = config;
    this.logger = logger;
    reload();
  }

  public NitroConfig getConfig() {
    return config;
  }

  public void enable() {
    if (config.isEnabled()) {
      logger.info("Enabling NitroBot...");
      try {
          this.api = JDABuilder.createDefault(config.getToken())
                  .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                  .setMemberCachePolicy(MemberCachePolicy.ALL)
                  .build();
          this.api.awaitReady();
          api.addEventListener(new NitroRedeemer(this, getConfig()));
          api.addEventListener(new NitroRemover(this, getConfig()));
          api.addEventListener(new NitroList(this, getConfig()));
          api.addEventListener(new NitroHelp(this, getConfig()));
          api.addEventListener(new NitroReload(this, getConfig()));
          api.addEventListener(new NitroAddAlert(this, getConfig()));
          api.addEventListener(new NitroRemoveAlert(this, getConfig()));
          logger.info("Discord Bot (NitroBot) is now active!");
          api.getGuildById(config.getServer()).updateCommands().addCommands(
                  Commands.slash("help", "List commands for the bot"),
                  Commands.slash("redeem", "Redeem Nitro perks for your account")
                          .addOption(OptionType.STRING, "username", "Your Minecraft username", true),
                  Commands.slash("config-reload", "Reload the configuration file"),
                  Commands.slash("list", "Display list of active Nitro Boosters or the current commands in configuration")
                          .addOption(OptionType.STRING, "list", "Which list to display", true, true),
                  Commands.slash("remove", "Remove Nitro perks from connected Minecraft account"),
                  Commands.slash("revoke", "Forcefully remove in-game Nitro perks from a user")
                          .addOption(OptionType.USER, "user", "User to revoke Nitro perks from", true)
                          .addOption(OptionType.STRING, "duration", "How long to revoke (e.g. 2h, 3d, 1w)", false)
          ).queue();
      } catch (Exception e) {
          logger.info("Failed to login to Discord:" + e.getMessage());
      }
    }
  }

  public Guild getServer() {
      return api.getGuildById(config.getServer());
  }

  private void setAPI(JDA api) {
    this.api = api;
  }

  public void disable() {
    if (this.api != null) {
      this.api.shutdown();
    }
    this.api = null;
  }

  public void alert(String message) {
    sendMessage(message, true);
  }

  public void sendMessage(String message, boolean alert) {
    if (api != null) {
        Guild guild = api.getGuildById(config.getServer());
        if (guild != null) {
            TextChannel textChannel = alert ? guild.getTextChannelById(config.getAlertChannel()) : guild.getTextChannelById(config.getMainChannel());
            if (textChannel != null) {
                if (alert) {
                    textChannel.sendMessage(message).queue();
                } else {
                    textChannel.sendMessage(message).queue(m -> m.delete().queueAfter(15, TimeUnit.SECONDS));
                }
            }
        }
    }
  }

  public void reload() {
    if (this.api != null && !config.isEnabled()) {
      disable();
    } else if (this.api == null && config.isEnabled()) {
      enable();
    }
  }
}
