package bot.string.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MessageDeleteListener extends ListenerAdapter {
    private static final Map<Long, Message> lastDeleted = new HashMap<>();
    private static final Map<Long, Message> lastMessages = new HashMap<>();

    public static Message getMsg(Long channelId) {
        return lastDeleted.get(channelId);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        lastMessages.put(event.getMessageIdLong(), event.getMessage());
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        var deleted = lastMessages.get(event.getMessageIdLong());
        lastDeleted.put(event.getChannel().getIdLong(), deleted);
        lastMessages.clear();
    }
}
