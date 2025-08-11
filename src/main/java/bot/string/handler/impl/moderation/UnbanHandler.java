package bot.string.handler.impl.moderation;

import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class UnbanHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        if (args.length < 2)
            return;

        var bannedMember = UserSnowflake.fromId(args[1]);
        guild.unban(bannedMember).queue(_ -> {
            channel.sendMessage(":thumbsup:").queue();
        },
        _ -> {
            channel.sendMessage("No banned user by that ID :thumbsdown:").queue();
        });
    }
}
