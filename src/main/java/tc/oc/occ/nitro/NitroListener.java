package tc.oc.occ.nitro;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserAddEvent;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

public class NitroListener implements Listener {

  private NitroCloudy plugin;
  private DiscordBot api;

  public NitroListener(NitroCloudy plugin, DiscordBot bot) {
    this.plugin = plugin;
    this.api = bot;
  }

  @EventHandler
  public void onNitroAdd(NitroUserAddEvent event) {
    String[] parts = (event.getUser().toString()).split(":");
    api.alert(
        ":white_check_mark: Nitro Boosting privileges have been granted to `"
            + parts[2]
            + "` (`"
            + parts[3]
            + "`) in-game. Claimed by `"
            + parts[0]
            + "` (`"
            + parts[1]
            + "`).");
    // Announce in console that the user has redeemed nitro privileges
    Bukkit.getConsoleSender()
        .sendMessage(
            "[Nitro] Nitro Booster "
                + parts[0]
                + " ("
                + parts[1]
                + ") has claimed in-game privileges for "
                + parts[2]
                + " ("
                + parts[3]
                + ")");
    String[] commandsToExecute =
        api.getConfig().getRedemptionCommand(event.getUser().getPlayerId().toString());
    // Print amount of commands to be executed
    Bukkit.getConsoleSender()
        .sendMessage("[Nitro] Executing " + commandsToExecute.length + " command(s)");
    // Print the commands
    for (int i = 0; i < commandsToExecute.length; i++) {
      Bukkit.getConsoleSender()
          .sendMessage("[Nitro] Command " + (i + 1) + ": " + commandsToExecute[i]);
      // If the command contains the placeholder %s, replace it with the player's name
      if (commandsToExecute[i].contains("%s")) {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), commandsToExecute[i].replace("%s", parts[2]));
      } else {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandsToExecute[i]);
      }
    }
    api.getConfig().save(plugin.getConfig());
    plugin.saveConfig();
  }

  @EventHandler
  public void onNitroRemove(NitroUserRemoveEvent event) {
    String[] parts = (event.getUser().toString()).split(":");
    api.alert(
        ":no_entry_sign: Nitro Boosting privileges have been removed from `"
            + parts[2]
            + " ("
            + parts[3]
            + "). The user `"
            + parts[0]
            + "` (`"
            + parts[1]
            + "`) is no longer boosting the server.");
    // Announce in console that the user has lost nitro
    Bukkit.getConsoleSender()
        .sendMessage(
            "[Nitro] Removing Nitro Boosting privileges from `"
                + parts[2]
                + "` (`"
                + parts[3]
                + "`). The user `"
                + parts[0]
                + "` (`"
                + parts[1]
                + "`) is no longer boosting the server.");
    String[] commandsToExecute =
        api.getConfig().getRemovalCommand(event.getUser().getPlayerId().toString());
    // Print amount of commands to be executed
    Bukkit.getConsoleSender()
        .sendMessage("[Nitro] Executing " + commandsToExecute.length + " command(s)");
    // Print the commands
    for (int i = 0; i < commandsToExecute.length; i++) {
      Bukkit.getConsoleSender()
          .sendMessage("[Nitro] Command " + (i + 1) + ": " + commandsToExecute[i]);
      if (commandsToExecute[i].contains("%s")) {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), commandsToExecute[i].replace("%s", parts[2]));
      } else {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandsToExecute[i]);
      }
    }
    api.getConfig().removeNitro(event.getUser());
    api.getConfig().save(plugin.getConfig());
    plugin.saveConfig();
  }
}
