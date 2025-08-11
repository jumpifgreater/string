package bot.string.handler.impl.moderation;

import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class NukeHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.MANAGE_CHANNEL)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }

        channel.sendMessage("Nuking channel... :bomb:").queue(success -> {
            var channelName = channel.getName();
            var category = channel.getParentCategory();

            channel.delete().queue(deleted -> {
                var createAction = guild.createTextChannel(channelName);

                if (category != null) {
                    createAction.setParent(category);
                }

                createAction.queue(newChannel -> {
                    newChannel.sendMessage("first").queue();
                });
            });
        });
    }
}
