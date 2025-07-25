import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;

public class CommandHelper extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild())
            return;

        var args = event.getMessage().getContentRaw().split(" ");
        var author = event.getMember();
        var guild = event.getGuild();
        var channel = event.getChannel().asTextChannel();

        switch (args[0]) {
            case ",vc" -> handleVCCommand(args, author, guild, channel);

            case ",r" -> {
                if (args.length < 3) return;

                var targetMember = Utils.getMentionedMember(args[1], guild);
                var role = guild.getRolesByName(args[2], true).stream().findFirst().orElse(null);
                if (targetMember != null && role != null) {
                    guild.addRoleToMember(targetMember, role).queue();
                    channel.sendMessage("role given").queue();
                }
            }

            case ",kick" -> {
                if (args.length < 2) return;

                var kickTarget = Utils.getMentionedMember(args[1], guild);
                if (kickTarget != null) {
                    kickTarget.kick().queue();
                    channel.sendMessage("user kicked").queue();
                }
            }

            case ",time" -> {
                if (args.length < 3) return;

                var timeoutTarget = Utils.getMentionedMember(args[1], guild);
                try {
                    int secs = Integer.parseInt(args[2]);
                    timeoutTarget.timeoutFor(Duration.ofSeconds(secs)).queue();
                    channel.sendMessage("user timed out").queue();
                } catch (Exception ignored) {}
            }
        }
    }

    private void handleVCCommand(String[] args, Member author, Guild guild, TextChannel channel) {
        if (args.length < 2) return;

        switch (args[1]) {
            case "lock" -> {
                var vc = VCManager.privateChannels.get(author.getIdLong());
                if (vc != null) {
                    vc.getManager().putPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
                    channel.sendMessage("vc locked").queue();
                }
            }

            case "kick" -> {
                if (args.length < 3) return;
                var target = Utils.getMentionedMember(args[2], guild);
                if (target != null) {
                    guild.kickVoiceMember(target).queue();
                    channel.sendMessage("user kicked from vc").queue();
                }
            }

            case "ban" -> {
                if (args.length < 3) return;
                var banned = Utils.getMentionedMember(args[2], guild);
                if (banned != null) {
                    VCManager.vcBans.getOrDefault(author.getIdLong(), new ArrayList<>()).add(banned.getIdLong());
                    guild.kickVoiceMember(banned).queue();
                    channel.sendMessage("user banned from vc").queue();
                }
            }
        }
    }
}