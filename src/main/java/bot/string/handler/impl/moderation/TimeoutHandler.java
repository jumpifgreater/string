package bot.string.handler.impl.moderation;

import bot.string.utils.Utils;
import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;

public class TimeoutHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.MODERATE_MEMBERS)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        if (args.length < 3) return;
        var timeoutTarget = Utils.getMentionedMember(args[1], guild);
        try {
            var secs = Integer.parseInt(args[2]);
            if (timeoutTarget != null) {
                timeoutTarget.timeoutFor(Duration.ofSeconds(secs)).queue();
                channel.sendMessage("User timed out for " + secs + " seconds").queue();
            } else {
                channel.sendMessage("User not found").queue();
            }
        } catch (Exception e) {
            channel.sendMessage("Invalid time format").queue();
        }
    }
}
