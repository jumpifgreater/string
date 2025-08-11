package bot.string.handler.impl.chat;

import bot.string.handler.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class HelpHandler implements CommandHandler {
    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
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
}
