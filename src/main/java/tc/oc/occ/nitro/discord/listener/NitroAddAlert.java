package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroAddAlert extends NitroListener {

  public NitroAddAlert(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        if(event.getMember().getRoles().contains(event.getGuild().getRoleById(config.getNitroRole()))) {
            if (config.removeExpiredRevocations()) {
                config.save(NitroCloudy.get().getConfig());
                NitroCloudy.get().saveConfig();
            }
            if (config.getRevocation(event.getUser().getId()).isPresent()) {
                api.sendMessage(
                        ":no_entry_sign: "
                                + event.getMember().getUser().getAsMention()
                                + ", your Nitro Boosting privileges are currently revoked. Contact a staff member if you believe this is a mistake.",
                        false);
                return;
            }
            api.sendMessage(
                    ":tada: Thanks for boosting the server, "
                            + event.getMember().getUser().getAsMention()
                            + "! Use `/redeem <Minecraft username>` to claim your in-game privileges. If you need assistance, use `/help` or contact a staff member.",
                    false);
        }

  }
}
