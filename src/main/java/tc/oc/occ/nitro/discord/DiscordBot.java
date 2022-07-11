package tc.oc.occ.nitro.discord;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.listener.*;

public class DiscordBot {

  private DiscordApi api;

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
      logger.info("Enabling Nitro DiscordBot...");
      new DiscordApiBuilder()
          .setToken(config.getToken())
          .setWaitForServersOnStartup(false)
          .setIntents(
              Intent.GUILDS, Intent.GUILD_MEMBERS, Intent.GUILD_PRESENCES, Intent.GUILD_MESSAGES)
          .login()
          .thenAcceptAsync(
              api -> {
                setAPI(api);
                api.setMessageCacheSize(1, 60 * 60);
                api.addListener(new NitroRedeemer(this, getConfig()));
                api.addListener(new NitroRemover(this, getConfig()));
                api.addListener(new NitroList(this, getConfig()));
                api.addListener(new NitroBan(this, getConfig()));
                api.addListener(new NitroHelp(this, getConfig()));
                api.addListener(new NitroReload(this, getConfig()));
                api.addListener(new NitroAddAlert(this, getConfig()));
                api.addListener(new NitroRemoveAlert(this, getConfig()));

                logger.info("Discord Bot (NitroCloudy) is now active!");
              });
    }
  }

  public Optional<Server> getServer() {
    return api.getServerById(config.getServer());
  }

  private void setAPI(DiscordApi api) {
    this.api = api;
  }

  public void disable() {
    if (this.api != null) {
      this.api.disconnect();
    }
    this.api = null;
  }

  public void alert(String message) {
    sendMessage(message, true);
  }

  public void sendMessage(String message, boolean alert) {
    if (api != null && !alert) {
      api.getServerById(config.getServer())
          .ifPresent(
              server ->
                  server
                      .getChannelById(config.getMainChannel())
                      .ifPresent(
                          channel ->
                              channel
                                  .asTextChannel()
                                  .ifPresent(
                                      text ->
                                          text.sendMessage(message)
                                              .thenAccept(
                                                  sentMessage ->
                                                      sentMessage
                                                          .getApi()
                                                          .getThreadPool()
                                                          .getScheduler()
                                                          .schedule(
                                                              () -> sentMessage.delete(),
                                                              15,
                                                              TimeUnit.SECONDS)))));
    } else if (api != null && alert) {
      api.getServerById(config.getServer())
          .ifPresent(
              server ->
                  server
                      .getChannelById(config.getAlertChannel())
                      .ifPresent(
                          channel ->
                              channel
                                  .asTextChannel()
                                  .ifPresent(text -> text.sendMessage(message))));
    }
  }

  public void deleteCommand(MessageCreateEvent event) {
    event
        .getMessage()
        .addReaction("✅")
        .thenAccept(
            reaction -> {
              event
                  .getApi()
                  .getThreadPool()
                  .getScheduler()
                  .schedule(() -> event.getMessage().delete(), 10, TimeUnit.SECONDS);
            });
  }

  public void reload() {
    if (this.api != null && !config.isEnabled()) {
      disable();
    } else if (this.api == null && config.isEnabled()) {
      enable();
    }
  }
}
