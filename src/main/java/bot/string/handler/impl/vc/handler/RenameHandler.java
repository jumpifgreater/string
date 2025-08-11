package bot.string.handler.impl.vc.handler;

import bot.string.handler.impl.vc.VCManager;
import bot.string.handler.impl.vc.VCCommandHandler;
import bot.string.handler.impl.vc.VCHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;

public class RenameHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        if (args.length < 3) {
            channel.sendMessage("Provide a new name for the VC").queue();
            return;
        }

        var vcOwner = VCHandler.getVCOwnerId(vc);
        if (author.getIdLong() != vcOwner && (VCManager.vcCoOwners.getOrDefault(vcOwner, new ArrayList<>()).stream().noneMatch(id -> id == author.getIdLong()))) {
            channel.sendMessage("Only the VC owner or a co-owner can rename the VC").queue();
            return;
        }

        var newName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        vc.getManager().setName(newName).queue(
                _ -> channel.sendMessage("VC renamed to **" + newName + "**").queue(),
                _ -> channel.sendMessage("Failed to rename VC").queue()
        );
    }
}
