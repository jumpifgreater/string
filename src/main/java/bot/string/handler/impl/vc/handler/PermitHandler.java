package bot.string.handler.impl.vc.handler;

import bot.string.utils.Utils;
import bot.string.handler.impl.vc.VCCommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.EnumSet;

public class PermitHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        switch (args[1]) {
            case "permit" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to unpermit").queue();
                    return;
                }

                var unpermitMember = Utils.getMentionedMember(args[2], guild);
                if (unpermitMember == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }

                vc.getManager().removePermissionOverride(unpermitMember).queue(
                        _ -> channel.sendMessage("User unpermitted from VC").queue(),
                        _ -> channel.sendMessage("Failed to unpermit user").queue()
                );
            }
            case "unpermit" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to permit").queue();
                    return;
                }

                var permitMember = Utils.getMentionedMember(args[2], guild);
                if (permitMember == null) {
                    channel.sendMessage("Could not find that member").queue();
                    return;
                }

                vc.getManager().putPermissionOverride(permitMember,
                        EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL),
                        null).queue(
                        _ -> channel.sendMessage("User permitted to join VC").queue(),
                        _ -> channel.sendMessage("Failed to permit user").queue()
                );
            }
        }
    }
}
