package bot.string.handler.impl.moderation;

import bot.string.utils.Utils;
import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.TimeUnit;

public class BanHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        if (args.length < 2)
            return;

        var banTarget = Utils.getMentionedMember(args[1], guild);
        if (banTarget != null) {
            try {
                banTarget.ban(7, TimeUnit.DAYS).queue();
                channel.sendMessage(":thumbsup:").queue();
            } catch (Exception e) {
                channel.sendMessage("User has higher roles than the bot").queue();
            }
        }
    }
}
