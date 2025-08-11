package bot.string.handler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface CommandHandler {
    void handle(String[] args, Member author, Guild guild, TextChannel channel);
}
