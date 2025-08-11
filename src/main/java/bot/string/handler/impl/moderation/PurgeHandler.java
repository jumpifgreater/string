package bot.string.handler.impl.moderation;

import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.TimeUnit;

public class PurgeHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.MANAGE_CHANNEL)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        if (args.length < 2) {
            channel.sendMessage("Usage: ,purge <amount>").queue();
            return;
        }

        try {
            int amt = Integer.parseInt(args[1]);
            if (amt <= 1 || amt >= 100) {
                channel.sendMessage("invalid amount").queue();
                return;
            }

            channel.getHistory().retrievePast(amt).queue(messages -> {
                channel.purgeMessages(messages);
                channel.sendMessage("deleted " + messages.size() + " messages").queue(msg -> {
                    msg.delete().queueAfter(5, TimeUnit.SECONDS);
                });
            });
        } catch (NumberFormatException _) {
            channel.sendMessage("provide a valid number").queue();
        }
    }
}
