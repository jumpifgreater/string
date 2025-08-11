package bot.string.handler.impl.vc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class VCManager extends ListenerAdapter {
    public static final Map<Long, VoiceChannel> privateChannels = new HashMap<>();
    public static final Map<Long, List<Long>> vcBans = new HashMap<>();
    public static final Map<Long, List<Long>> vcCoOwners = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        var member = event.getMember();
        var guild = event.getGuild();
        var joined = event.getChannelJoined();
        var left = event.getChannelLeft();

        if (joined != null && joined.getName().equalsIgnoreCase("Join to Create")) {
            guild.createVoiceChannel(member.getEffectiveName() + "'s channel", joined.getParentCategory())
                    .addPermissionOverride(guild.getPublicRole(),   EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT), null)
                    .addPermissionOverride(member,                  EnumSet.of(Permission.VOICE_CONNECT, Permission.MANAGE_CHANNEL), null)
                    .queue(vc -> {
                        privateChannels.put(member.getIdLong(), vc);
                        vcBans.put(member.getIdLong(), new ArrayList<>());
                        vcCoOwners.put(member.getIdLong(), new ArrayList<>());
                        guild.moveVoiceMember(member, vc).queue();
                    });
            return;
        }

        if (left != null && privateChannels.containsValue(left) && left.getMembers().isEmpty()) {
            privateChannels.values().remove(left);
            vcBans.entrySet().removeIf(e -> e.getKey().equals(getOwnerIdByVC((VoiceChannel) left)));
            vcCoOwners.entrySet().removeIf(e -> e.getKey().equals(getOwnerIdByVC((VoiceChannel) left)));
            left.delete().queue();
        }

        if (joined != null) {
            for (var entry : privateChannels.entrySet()) {
                var ownerID = entry.getKey();
                var vc = entry.getValue();

                if (!joined.equals(vc))
                    continue;

                var bannedList = vcBans.get(ownerID);
                if (bannedList != null && bannedList.contains(member.getIdLong())) {
                    guild.kickVoiceMember(member).queue();
                    break;
                }
            }
        }
    }

    private long getOwnerIdByVC(VoiceChannel vc) {
        for (var entry : privateChannels.entrySet()) {
            if (!entry.getValue().equals(vc))
                continue;

            return entry.getKey();
        }

        return -1L;
    }
}
