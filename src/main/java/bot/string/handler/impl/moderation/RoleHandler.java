package bot.string.handler.impl.moderation;

import bot.string.utils.Utils;
import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;

public class RoleHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        switch (args[0]) {
            case ",r" -> handleGive(args, author, guild, channel);
            case ",rr" -> handleRemove(args, author, guild, channel);
            case ",cr" -> handleCreate(args, author, guild, channel);
            case ",rl" -> handleListRoles(args, author, guild, channel);
        }
    }

    private void handleRemove(String[] args, Member author, Guild guild, TextChannel channel) {
        if (!author.hasPermission(Permission.ADMINISTRATOR)) {
            channel.sendMessage("You don't have permission to use this command.").queue();
            return;
        }
        if (args.length < 3) return;

        var targetMember = Utils.getMentionedMember(args[1], guild);
        var role = guild.getRolesByName(args[2], true).stream().findFirst().orElse(null);

        if (targetMember != null && role != null) {
            guild.removeRoleFromMember(targetMember, role).queue(
                    _ -> channel.sendMessage("Role removed from user").queue(),
                    _ -> channel.sendMessage("Failed to remove role").queue()
            );
        } else {
            channel.sendMessage("User or role not found").queue();
        }
    }

    private void handleGive(String[] args, Member author, Guild guild, TextChannel channel) {
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

    private void handleCreate(String[] args, Member author, Guild guild, TextChannel channel) {
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
                        _ -> channel.sendMessage("Failed to create role").queue()
                );
    }

    private void handleListRoles(String[] args, Member author, Guild guild, TextChannel channel) {
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
}
