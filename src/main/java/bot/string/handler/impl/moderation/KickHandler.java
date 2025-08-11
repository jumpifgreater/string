package bot.string.handler.impl.moderation;

import bot.string.utils.Utils;
import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class KickHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        if (args.length < 2)
            return;

        var kickTarget = Utils.getMentionedMember(args[1], guild);
        if (kickTarget != null) {
            kickTarget.kick().queue();
            channel.sendMessage("User kicked").queue();
        }
    }
}
