package bot.string.handler.impl.vc.handler;

import bot.string.handler.impl.vc.VCCommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.EnumSet;

public class LockHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        switch (args[1]) {
            case "lock" -> {
                vc.getManager().putPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
                channel.sendMessage("VC locked").queue();
            }
            case "unlock" -> {
                vc.getManager().putPermissionOverride(guild.getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT), null).queue();
                channel.sendMessage("VC unlocked").queue();
            }
        }
    }
}
