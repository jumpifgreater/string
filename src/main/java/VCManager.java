import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class VCManager extends ListenerAdapter {
    public static final Map<Long, VoiceChannel> privateChannels = new HashMap<>();
    public static final Map<Long, List<Long>> vcBans = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        var member = event.getMember();
        var guild = event.getGuild();
        var joined = event.getChannelJoined();

        if (joined != null && joined.getName().equalsIgnoreCase("Join to Create")) {
            guild.createVoiceChannel(member.getEffectiveName() + "'s channel", joined.getParentCategory())
                    .addPermissionOverride(guild.getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), EnumSet.of(Permission.VOICE_CONNECT))
                    .addPermissionOverride(member, EnumSet.of(Permission.VOICE_CONNECT, Permission.MANAGE_CHANNEL), null)
                    .queue(vc -> {
                        privateChannels.put(member.getIdLong(), vc);
                        vcBans.put(member.getIdLong(), new ArrayList<>());
                        guild.moveVoiceMember(member, vc).queue();
                    });
        }

        var left = event.getChannelLeft();
        if (left != null && privateChannels.containsValue(left) && left.getMembers().isEmpty()) {
            left.delete().queue();
            privateChannels.values().remove(left);
        }

        for (Map.Entry<Long, VoiceChannel> entry : VCManager.privateChannels.entrySet()) {
            var ownerID = entry.getKey();
            var vc = entry.getValue();

            if (joined != null && joined.equals(vc)) {
                var banned = VCManager.vcBans.get(ownerID);
                if (banned != null && banned.contains(member.getIdLong())) {
                    guild.kickVoiceMember(member).queue();
                    break;
                }
            }
        }
    }
}
