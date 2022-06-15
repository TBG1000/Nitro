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
    // Iterate through the list of strings from redemption-commands in the config
    for (String redemptionCommand :
        api.getConfig().getRedemptionCommands(event.getUser().getPlayerId().toString())) {
      // Print the redemption command(s) in console
      Bukkit.getConsoleSender().sendMessage("[Nitro] Executing redemption command: " + redemptionCommand);
      // Execute the command
      if (redemptionCommand.contains("%s")) {
        Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), redemptionCommand.replace("%s", parts[2]));
      } else {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), redemptionCommand);
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
            + "` (`"
            + parts[3]
            + "`). The user `"
            + parts[0]
            + "` (`"
            + parts[1]
            + "`) is no longer boosting the server.");
    // Announce in console that the user has lost nitro
    Bukkit.getConsoleSender()
        .sendMessage(
            "[Nitro] Removing Nitro Boosting privileges from "
                + parts[2]
                + " ("
                + parts[3]
                + "). The user "
                + parts[0]
                + " ("
                + parts[1]
                + ") is no longer boosting the server.");
    // Iterate through the list of strings from removal-commands in the config
    for (String removalCommand :
        api.getConfig().getRemovalCommands(event.getUser().getPlayerId().toString())) {
      // Print the removal command(s) in console
      Bukkit.getConsoleSender().sendMessage("[Nitro] Executing removal command: " + removalCommand);
      // Execute the command
      if (removalCommand.contains("%s")) {
        Bukkit.getServer()
            .dispatchCommand(Bukkit.getConsoleSender(), removalCommand.replace("%s", parts[2]));
      } else {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), removalCommand);
      }
    }
    api.getConfig().removeNitro(event.getUser());
    api.getConfig().save(plugin.getConfig());
    plugin.saveConfig();
  }
}
