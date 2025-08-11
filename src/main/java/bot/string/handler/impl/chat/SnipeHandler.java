package bot.string.handler.impl.chat;

import bot.string.handler.CommandHandler;
import bot.string.listeners.MessageDeleteListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class SnipeHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        var msg = MessageDeleteListener.getMsg(channel.getIdLong());
        if (msg == null) {
            channel.sendMessage("nothing to be sniped").queue();
            return;
        }

        var embed = new EmbedBuilder();
        embed.setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl());
        embed.setDescription(msg.getContentRaw());
        embed.setFooter("sniped by " + author.getUser().getName(), author.getUser().getEffectiveAvatarUrl());
        embed.setTimestamp(msg.getTimeCreated());
        embed.setColor(Color.BLACK);

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
