package bot.string;

import bot.string.handler.CommandHandler;
import bot.string.handler.impl.chat.HelpHandler;
import bot.string.handler.impl.moderation.*;
import bot.string.handler.impl.vc.VCHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

//TODO: impl ,vc claim

public class CommandManager extends ListenerAdapter {
    private final Map<String, CommandHandler> commands = new HashMap<>() {
        {
            put(",help",    new HelpHandler());
            put(",vc",      new VCHandler());

            put(",ban",     new BanHandler());
            put(",unban",   new UnbanHandler());
            put(",kick",    new KickHandler());
            put(",n",       new NukeHandler());
            put(",timeout", new TimeoutHandler());
            put(",purge",   new PurgeHandler());

            var roles = new RoleHandler();
            put(",rl",      roles);
            put(",r",       roles);
            put(",cr",      roles);
            put(",rr",      roles);
        }
    };

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild())
            return;

        var args = event.getMessage().getContentRaw().split(" ");
        var author = event.getMember();
        var guild = event.getGuild();
        var channel = event.getChannel().asTextChannel();

        var handler = commands.get(args[0]);
        if(handler == null)
            return;

        handler.handle(args, author, guild, channel);
    }
}
