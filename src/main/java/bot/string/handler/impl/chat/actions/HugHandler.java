package bot.string.handler.impl.chat.actions;

import bot.string.handler.CommandHandler;
import bot.string.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class HugHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (args.length < 2) {
            channel.sendMessage("invalid args").queue();
            return;
        }

        var target = Utils.getMentionedMember(args[1], guild);

        if (target == null)
            return;

        var embed = new EmbedBuilder();
        embed.setAuthor(author.getEffectiveName(), null, author.getEffectiveAvatarUrl());
        embed.setDescription("***" + author.getAsMention() + " hugs " + target.getAsMention() + " tightly***");
        embed.setFooter("hugged by " + author.getUser().getName());
        embed.setColor(Color.PINK);

        channel.sendMessage(author.getAsMention() + target.getAsMention()).setEmbeds(embed.build()).queue();
    }
}
