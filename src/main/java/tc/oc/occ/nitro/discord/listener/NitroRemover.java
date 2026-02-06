package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.DurationUtils;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.data.NitroRevocation;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

import java.time.Instant;

public class NitroRemover extends NitroListener{

  public NitroRemover(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("remove")) {
            Member member = event.getMember();
            String discordID = member.getId();
            if (!config.getUser(discordID).isPresent()) {
                event.reply(":no_entry_sign: You have not yet redeemed your Nitro Boosting perks. Use `/redeem` to claim them! For more information, use `/help`.").setEphemeral(true).queue();
                return;
            }
            NitroUser nitroUser = config.getUser(discordID).get();
            NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitroUser));
            event.reply(":white_check_mark: " + member.getAsMention() + " You have removed Nitro Boosting privileges from `" + nitroUser.getMinecraftUsername() + "` (`" + nitroUser.getPlayerId().toString() + "`). You may use `/redeem` to redeem them again.").setEphemeral(true).queue();
        } else if (event.getName().equals("revoke")){
            OptionMapping messageOption = event.getOption("user");
            if (messageOption == null) return;
            OptionMapping durationOption = event.getOption("duration");
            if (!event.getChannelId().equals(config.getStaffChannel())) {
                event.reply(":warning: Run this command in staff channel!").setEphemeral(true).queue();
                return;
            }
            User targetUser = messageOption.getAsUser();
            String discordId = targetUser.getId();
            String discordName = targetUser.getName();
            Long durationMillis = null;
            if (durationOption != null) {
                String durationInput = durationOption.getAsString();
                if (!DurationUtils.isIndefinite(durationInput)) {
                    durationMillis = DurationUtils.parseDurationMillis(durationInput);
                }
                if (durationMillis == null && !DurationUtils.isIndefinite(durationInput)) {
                    event.reply(":warning: Invalid duration. Use formats like `30m`, `2h`, `3d`, or `1w2d`.").setEphemeral(true).queue();
                    return;
                }
            }
            Long expiresAt = durationMillis == null ? null : Instant.now().toEpochMilli() + durationMillis;
            NitroRevocation revocation = config.addRevocation(discordName, discordId, expiresAt);
            config.save(NitroCloudy.get().getConfig());
            NitroCloudy.get().saveConfig();
            if (config.getUser(discordId).isPresent()) {
                NitroUser user = config.getUser(discordId).get();
                NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(user));
            }
            String durationMessage =
                revocation.getExpiresAt() == null
                    ? "indefinitely"
                    : "for " + DurationUtils.formatDuration(durationMillis);
            event.reply(":triangular_flag_on_post: Revoked Nitro Boosting privileges from `"
                    + discordName
                    + "` (`"
                    + discordId
                    + "`) "
                    + durationMessage
                    + ".").queue();
            api.alert(
                    ":triangular_flag_on_post: `"
                            + event.getUser().getName()
                            + "` (`"
                            + event.getUser().getId()
                            + "`) has revoked Nitro Boosting privileges from `"
                            + discordName
                            + "` (`"
                            + discordId
                            + "`) "
                            + durationMessage
                            + ".");

        }
    }
}
