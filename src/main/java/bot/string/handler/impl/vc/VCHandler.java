package bot.string.handler.impl.vc;

import bot.string.handler.CommandHandler;
import bot.string.handler.impl.vc.handler.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VCHandler implements CommandHandler {
    private final Map<String, VCCommandHandler> commandHandlers = new HashMap<>() {
        {
            var moderation  = new ModerationHandler();
            var permits     = new PermitHandler();
            var promotes    = new PromoteHandler();
            var locks       = new LockHandler();

            put("limit",    new LimitHandler());
            put("rename",   new RenameHandler());
            put("promote",  promotes);
            put("demote",   promotes);

            put("ban",      moderation);
            put("unban",    moderation);
            put("kick",     moderation);

            put("permit",   permits);
            put("unpermit", permits);

            put("lock",     locks);
            put("unlock",   locks);
        }
    };

    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if(handleMisInput(args, channel))
            return;

        handleVCCommand(args, author, guild, channel);
    }

    private VoiceChannel getVCForUser(Member user) {
        var vc = VCManager.privateChannels.get(user.getIdLong());
        if (vc != null)
            return vc;

        for (var entry : VCManager.vcCoOwners.entrySet()) {
            if (entry.getValue().contains(user.getIdLong()))
                return VCManager.privateChannels.get(entry.getKey());
        }

        return null;
    }

    private void handleVCCommand(String[] args, Member author, Guild guild, TextChannel channel) {
        if (args.length < 2) return;
        var vc = getVCForUser(author);
        if (vc == null) {
            channel.sendMessage("You don't have a private VC.").queue();
            return;
        }

        var handler = commandHandlers.get(args[1]);
        if(handler == null)
            return;

        handler.handle(args, author, guild, channel, vc);
    }

    private boolean handleMisInput(String[] args, TextChannel channel) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("help")) {
            var embed = new EmbedBuilder()
                    .setTitle("Voice Channel Commands Help")
                    .setDescription("""
                        `,vc lock` - Lock your private VC (deny connect for @everyone)
                        `,vc unlock` - Unlock your private VC (allow connect for @everyone)
                        `,vc permit <user>` - Allow a user to join your VC
                        `,vc unpermit <user>` - Remove a user's permission to join your VC
                        `,vc kick <user>` - Kick a user from your VC (Owner only)
                        `,vc ban <user>` - Ban a user from your VC (Owner only)
                        `,vc unban <user>` - Unban a user from your VC (Owner only)
                        `,vc limit <number>` - Set user limit on your VC (0 to remove)
                        `,vc promote <user>` - Promote user to VC co-owner (Owner only)
                        `,vc demote <user>` - Demote a VC co-owner (Owner only)
                        `,vc rename <new name>` - Rename your VC (Owner or co-owner)
                        """)
                    .setColor(Color.CYAN);
            channel.sendMessageEmbeds(embed.build()).queue();
            return true;
        }

        return false;
    }

    public static long getVCOwnerId(VoiceChannel vc) {
        for (var entry : VCManager.privateChannels.entrySet()) {
            if (entry.getValue().equals(vc)) return entry.getKey();
        }
        return -1L;
    }
}
