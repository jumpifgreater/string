package bot.string.handler.impl.chat.games;

import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.TimeUnit;

public class CoinFlipHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        var choice = args[1];

        var res = Math.random() < 0.5 ? "heads" : "tails";
        channel.sendMessage("flipping.").queue(msg -> {
            msg.editMessage("flipping..").queueAfter(500, TimeUnit.MILLISECONDS, m1 -> {
                m1.editMessage("flipping...").queueAfter(500, TimeUnit.MILLISECONDS, m2 -> {
                    if (choice.equals(res)) {
                        m2.editMessage(author.getAsMention() + ", you flipped: **" + res + "** AND WON :coin:").queue();
                        return;
                    }
                    m2.editMessage(author.getAsMention() + ", you flipped: **" + res + "** and lost... :clown:").queue();
                    return;
                });
            });
        });
    }
}
