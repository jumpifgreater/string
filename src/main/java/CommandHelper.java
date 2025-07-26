import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

//TODO: impl ,vc claim

public class CommandHelper extends ListenerAdapter {
    private VoiceChannel getVCForUser(Member user) {
        var vc = VCManager.privateChannels.get(user.getIdLong());
        if (vc != null) return vc;
        for (var entry : VCManager.vcCoOwners.entrySet()) {
            if (entry.getValue().contains(user.getIdLong())) return VCManager.privateChannels.get(entry.getKey());
        }
        return null;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild()) return;
        var args = event.getMessage().getContentRaw().split(" ");
        var author = event.getMember();
        var guild = event.getGuild();
        var channel = event.getChannel().asTextChannel();

        switch (args[0]) {
            case ",help" -> {
                var embed = new EmbedBuilder()
                        .setTitle("Help - Commands")
                        .setDescription("""
                    **General Commands:**
                    `,help` - Show this help message
                    `,r <user> <role>` - Give role to user (Admin only)
                    `,rr <user> <role>` - Remove role from user (Admin only)
                    `,cr <role name>` - Create a new role (Admin only)
                    `,rl` - List all roles in the server
                    `,kick <user>` - Kick user from server (Admin only)
                    `,time <user> <seconds>` - Timeout a user (Admin only)
                    `,n` - Delete the channel and create the same one (Admin only)
                    `,vc help` - Show voice channel commands help
                    """)
                        .setColor(Color.BLUE);
                channel.sendMessageEmbeds(embed.build()).queue();
            }

            case ",vc" -> {
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
                    return;
                }
                handleVCCommand(args, author, guild, channel);
            }

            case ",r" -> {
                if (!author.hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }
                if (args.length < 3) return;
                var targetMember = Utils.getMentionedMember(args[1], guild);
                var role = guild.getRolesByName(args[2], true).stream().findFirst().orElse(null);
                if (targetMember != null && role != null) {
                    guild.addRoleToMember(targetMember, role).queue();
                    channel.sendMessage("Role given").queue();
                } else {
                    channel.sendMessage("User or role not found").queue();
                }
            }

            case ",rr" -> {
                if (!author.hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }
                if (args.length < 3) return;

                var targetMember = Utils.getMentionedMember(args[1], guild);
                var role = guild.getRolesByName(args[2], true).stream().findFirst().orElse(null);

                if (targetMember != null && role != null) {
                    guild.removeRoleFromMember(targetMember, role).queue(
                            success -> channel.sendMessage("Role removed from user").queue(),
                            failure -> channel.sendMessage("Failed to remove role").queue()
                    );
                } else {
                    channel.sendMessage("User or role not found").queue();
                }
            }

            case ",cr" -> {
                if (!author.hasPermission(Permission.MANAGE_ROLES)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }

                if (args.length < 2) {
                    channel.sendMessage("Usage: ,cr <role name>").queue();
                    return;
                }

                var roleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                guild.createRole()
                        .setName(roleName)
                        .setPermissions(EnumSet.noneOf(Permission.class))
                        .queue(
                                role -> channel.sendMessage("Role created: `" + role.getName() + "`").queue(),
                                error -> channel.sendMessage("Failed to create role").queue()
                        );
            }

            case ",rl" -> {
                var roles = guild.getRoles();
                var builder = new StringBuilder();

                for (int i = roles.size() - 1; i >= 0; i--) {
                    var role = roles.get(i);
                    builder.append(role.getAsMention()).append(" (").append(role.getName()).append(")").append("\n");
                }

                var embed = new EmbedBuilder()
                        .setTitle("Server Roles")
                        .setDescription(builder.length() > 0 ? builder.toString() : "No roles found.")
                        .setColor(Color.CYAN);

                channel.sendMessageEmbeds(embed.build()).queue();
            }

            case ",kick" -> {
                if (!author.hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }

                if (args.length < 2) return;
                var kickTarget = Utils.getMentionedMember(args[1], guild);
                if (kickTarget != null) {
                    kickTarget.kick().queue();
                    channel.sendMessage("User kicked").queue();
                }
            }
            case ",time" -> {
                if (!author.hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }

                if (args.length < 3) return;
                var timeoutTarget = Utils.getMentionedMember(args[1], guild);
                try {
                    var secs = Integer.parseInt(args[2]);
                    if (timeoutTarget != null) {
                        timeoutTarget.timeoutFor(Duration.ofSeconds(secs)).queue();
                        channel.sendMessage("User timed out for " + secs + " seconds").queue();
                    } else {
                        channel.sendMessage("User not found").queue();
                    }
                } catch (Exception e) {
                    channel.sendMessage("Invalid time format").queue();
                }
            }

            case ",n" -> {
                if (!author.hasPermission(Permission.MANAGE_CHANNEL)) {
                    channel.sendMessage("You don't have permission to use this command.").queue();
                    return;
                }

                channel.sendMessage("Nuking channel... :bomb:").queue(success -> {
                    String channelName = channel.getName();
                    var server = channel.getGuild();
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
    }

    private void handleVCCommand(String[] args, Member author, Guild guild, TextChannel channel) {
        if (args.length < 2) return;
        var vc = getVCForUser(author);
        if (vc == null) {
            channel.sendMessage("You don't have a private VC.").queue();
            return;
        }
        switch (args[1]) {
            case "lock" -> {
                vc.getManager().putPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
                channel.sendMessage("VC locked").queue();
            }
            case "unlock" -> {
                vc.getManager().putPermissionOverride(guild.getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT), null).queue();
                channel.sendMessage("VC unlocked").queue();
            }
            case "permit" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to permit").queue();
                    return;
                }
                var permitMember = Utils.getMentionedMember(args[2], guild);
                if (permitMember == null) {
                    channel.sendMessage("Could not find that member").queue();
                    return;
                }
                vc.getManager().putPermissionOverride(permitMember,
                        EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL),
                        null).queue(
                        success -> channel.sendMessage("User permitted to join VC").queue(),
                        failure -> channel.sendMessage("Failed to permit user").queue()
                );
            }
            case "kick" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to kick").queue();
                    return;
                }
                var target = Utils.getMentionedMember(args[2], guild);
                if (target == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                if (author.getIdLong() != getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can kick users").queue();
                    return;
                }
                if (target.getVoiceState() != null && target.getVoiceState().inAudioChannel()) {
                    guild.kickVoiceMember(target).queue(
                            success -> channel.sendMessage("User kicked from VC").queue(),
                            failure -> channel.sendMessage("Failed to kick user").queue()
                    );
                } else {
                    channel.sendMessage("User is not in a voice channel").queue();
                }
            }
            case "ban" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to ban").queue();
                    return;
                }
                var banned = Utils.getMentionedMember(args[2], guild);
                if (banned == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                if (author.getIdLong() != getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can ban users").queue();
                    return;
                }
                if (banned.getVoiceState() != null && banned.getVoiceState().inAudioChannel()
                        && banned.getVoiceState().getChannel().equals(vc)) {
                    VCManager.vcBans.computeIfAbsent(author.getIdLong(), k -> new ArrayList<>()).add(banned.getIdLong());
                    guild.kickVoiceMember(banned).queue(
                            success -> channel.sendMessage("User banned from VC").queue(),
                            failure -> channel.sendMessage("Failed to ban user").queue()
                    );
                } else {
                    channel.sendMessage("User is not in your voice channel").queue();
                }
            }
            case "unban" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to unban").queue();
                    return;
                }
                var unbanned = Utils.getMentionedMember(args[2], guild);
                if (unbanned == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                if (author.getIdLong() != getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can unban users").queue();
                    return;
                }
                var bans = VCManager.vcBans.get(author.getIdLong());
                if (bans != null && bans.remove(unbanned.getIdLong())) {
                    channel.sendMessage("User unbanned from VC").queue();
                } else {
                    channel.sendMessage("User was not banned").queue();
                }
            }
            case "unpermit" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to unpermit").queue();
                    return;
                }
                var unpermitMember = Utils.getMentionedMember(args[2], guild);
                if (unpermitMember == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                vc.getManager().removePermissionOverride(unpermitMember).queue(
                        success -> channel.sendMessage("User unpermitted from VC").queue(),
                        failure -> channel.sendMessage("Failed to unpermit user").queue()
                );
            }
            case "limit" -> {
                if (args.length < 3) {
                    channel.sendMessage("Provide a user limit (0 to remove)").queue();
                    return;
                }
                try {
                    var limit = Integer.parseInt(args[2]);
                    if (limit < 0 || limit > 99) {
                        channel.sendMessage("Limit between 0 and 99").queue();
                        return;
                    }
                    vc.getManager().setUserLimit(limit == 0 ? 0 : limit).queue(
                            success -> channel.sendMessage(limit == 0 ? "User limit removed" : "User limit set to " + limit).queue(),
                            failure -> channel.sendMessage("Failed to set user limit").queue()
                    );
                } catch (Exception ignored) {
                    channel.sendMessage("Invalid number format").queue();
                }
            }

            case "promote" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to promote").queue();
                    return;
                }
                var promoted = Utils.getMentionedMember(args[2], guild);
                if (promoted == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                if (author.getIdLong() != getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can promote users").queue();
                    return;
                }
                VCManager.vcCoOwners.computeIfAbsent(author.getIdLong(), k -> new ArrayList<>()).add(promoted.getIdLong());
                vc.getManager().putPermissionOverride(promoted,
                        EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL),
                        null).queue(
                        success -> channel.sendMessage("User promoted to VC co-owner").queue(),
                        failure -> channel.sendMessage("Failed to promote user").queue());
            }

            case "demote" -> {
                if (args.length < 3) {
                    channel.sendMessage("Mention a user to demote").queue();
                    return;
                }
                var demoted = Utils.getMentionedMember(args[2], guild);
                if (demoted == null) {
                    channel.sendMessage("User not found").queue();
                    return;
                }
                if (author.getIdLong() != getVCOwnerId(vc)) {
                    channel.sendMessage("Only the VC owner can demote users").queue();
                    return;
                }
                var coOwners = VCManager.vcCoOwners.get(author.getIdLong());
                if (coOwners != null && coOwners.remove(demoted.getIdLong())) {
                    vc.getManager().removePermissionOverride(demoted).queue(
                            success -> channel.sendMessage("User demoted from VC co-owner").queue(),
                            failure -> channel.sendMessage("Failed to demote user").queue()
                    );
                } else {
                    channel.sendMessage("User is not a co-owner").queue();
                }
            }

            case "rename" -> {
                if (args.length < 3) {
                    channel.sendMessage("Provide a new name for the VC").queue();
                    return;
                }

                if (author.getIdLong() != getVCOwnerId(vc) &&
                        (VCManager.vcCoOwners.getOrDefault(getVCOwnerId(vc), new ArrayList<>()).stream()
                                .noneMatch(id -> id == author.getIdLong()))) {
                    channel.sendMessage("Only the VC owner or a co-owner can rename the VC").queue();
                    return;
                }

                var newName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                vc.getManager().setName(newName).queue(
                        success -> channel.sendMessage("VC renamed to **" + newName + "**").queue(),
                        failure -> channel.sendMessage("Failed to rename VC").queue()
                );
            }
        }
    }

    private long getVCOwnerId(VoiceChannel vc) {
        for (var entry : VCManager.privateChannels.entrySet()) {
            if (entry.getValue().equals(vc)) return entry.getKey();
        }
        return -1L;
    }

}
