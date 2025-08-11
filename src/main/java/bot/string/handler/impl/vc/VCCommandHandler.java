package bot.string.handler.impl.vc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public interface VCCommandHandler {
    void handle(String[] args, Member author, Guild guild, TextChannel channel, VoiceChannel vc);
}
