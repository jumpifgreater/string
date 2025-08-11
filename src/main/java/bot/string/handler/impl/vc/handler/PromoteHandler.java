package bot.string.handler.impl.vc.handler;

import bot.string.utils.Utils;
import bot.string.handler.impl.vc.VCManager;
import bot.string.handler.impl.vc.VCCommandHandler;
import bot.string.handler.impl.vc.VCHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;
import java.util.EnumSet;

public class PromoteHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        switch (args[1]) {
            case "promote" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to demote").queue();
                    return;
                }

                var demoted = Utils.getMentionedMember(args[2], guild);
                if (demoted == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }

                if (author.getIdLong() != VCHandler.getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can demote users").queue();
                    return;
                }

                var coOwners = VCManager.vcCoOwners.get(author.getIdLong());
                if (coOwners != null && coOwners.remove(demoted.getIdLong())) {
                    vc.getManager().removePermissionOverride(demoted).queue(
                            success -> channel.sendMessage("User demoted from VC co-owner").queue(),
                            failure -> channel.sendMessage("Failed to demote user").queue()
                    ); // bug
                } else {
                    channel.sendMessage("User is not a co-owner").queue();
                }
            }
            case "demote" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to promote").queue();
                    return;
                }

                var promoted = Utils.getMentionedMember(args[2], guild);
                if (promoted == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }

                if (author.getIdLong() != VCHandler.getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can promote users").queue();
                    return;
                }

                VCManager.vcCoOwners.computeIfAbsent(author.getIdLong(), k -> new ArrayList<>()).add(promoted.getIdLong());
                vc.getManager().putPermissionOverride(promoted,
                        EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL),
                        null).queue(
                        _ -> channel.sendMessage("User promoted to VC co-owner").queue(),
                        _ -> channel.sendMessage("Failed to promote user").queue());
            }
        }
    }
}
