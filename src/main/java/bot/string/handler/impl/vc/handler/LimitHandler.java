package bot.string.handler.impl.vc.handler;

import bot.string.handler.impl.vc.VCCommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class LimitHandler implements VCCommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc) {
        if (args.length < 3) {
            channel.sendMessage("Provide a user limit (0 to remove)").queue();
            return;
        }
        try {
            var limit = Integer.parseInt(args[2]);
            if (limit < 0 || limit > 99) {
                channel.sendMessage("Limit between 0 and 99").queue();
                return;
            }

            vc.getManager().setUserLimit(limit == 0 ? 0 : limit).queue(
                    _ -> channel.sendMessage(limit == 0 ? "User limit removed" : "User limit set to " + limit).queue(),
                    _ -> channel.sendMessage("Failed to set user limit").queue()
            );
        } catch (Exception ignored) {
            channel.sendMessage("Invalid number format").queue();
        }
    }
}
