package bot.string.handler.impl.vc.handler;

import bot.string.utils.Utils;
import bot.string.handler.impl.vc.VCManager;
import bot.string.handler.impl.vc.VCCommandHandler;
import bot.string.handler.impl.vc.VCHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;

public class ModerationHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        switch (args[1]) {
            case "kick" -> kick(args, author, guild, channel, vc);
            case "ban" -> ban(args, author, guild, channel, vc);
            case "unban" -> unban(args, author, guild, channel, vc);
        }
    }

    private void kick(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        if (args.length < 3) {
            channel.sendMessage("Mention a user to kick").queue();
            return;
        }

        var target = Utils.getMentionedMember(args[2], guild);
        if (target == null) {
            channel.sendMessage("User not found").queue();
            return;
        }

        if (author.getIdLong() != VCHandler.getVCOwnerId(vc)) {
            channel.sendMessage("Only the VC owner can kick users").queue();
            return;
        }

        if (target.getVoiceState() != null && target.getVoiceState().inAudioChannel()) {
            guild.kickVoiceMember(target).queue(
                    _ -> channel.sendMessage("User kicked from VC").queue(),
                    _ -> channel.sendMessage("Failed to kick user").queue()
            );
        } else {
            channel.sendMessage("User is not in a voice channel").queue();
        }
    }

    private void ban(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        if (args.length < 3) {
            channel.sendMessage("Mention a user to ban").queue();
            return;
        }

        var banned = Utils.getMentionedMember(args[2], guild);
        if (banned == null) {
            channel.sendMessage("User not found").queue();
            return;
        }

        if (author.getIdLong() != VCHandler.getVCOwnerId(vc)) {
            channel.sendMessage("Only the VC owner can ban users").queue();
            return;
        }

        if (banned.getVoiceState() != null && banned.getVoiceState().inAudioChannel()
                && banned.getVoiceState().getChannel().equals(vc)) {
            VCManager.vcBans.computeIfAbsent(author.getIdLong(), _ -> new ArrayList<>()).add(banned.getIdLong());

            guild.kickVoiceMember(banned).queue(
                    _ -> channel.sendMessage("User banned from VC").queue(),
                    _ -> channel.sendMessage("Failed to ban user").queue()
            );
        } else {
            channel.sendMessage("User is not in your voice channel").queue();
        }
    }

    private void unban(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        if (args.length < 3) {
            channel.sendMessage("Mention a user to unban").queue();
            return;
        }

        var unbanned = Utils.getMentionedMember(args[2], guild);
        if (unbanned == null) {
            channel.sendMessage("User not found").queue();
            return;
        }

        if (author.getIdLong() != VCHandler.getVCOwnerId(vc)) {
            channel.sendMessage("Only the VC owner can unban users").queue();
            return;
        }

        var bans = VCManager.vcBans.get(author.getIdLong());
        if (bans != null && bans.remove(unbanned.getIdLong())) {
            channel.sendMessage("User unbanned from VC").queue();
        } else {
            channel.sendMessage("User was not banned").queue();
        }
    }
}
