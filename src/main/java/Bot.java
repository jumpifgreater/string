import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.entities.channel.concrete.VoiceChannelImpl;
import net.dv8tion.jda.internal.handle.ApplicationCommandPermissionsUpdateHandler;

public class Bot {
    public static void main(String[] args) {
        JDABuilder.createDefault("MTM5ODM3NTc5MDc0MzM5MjI2Ng.GFCvNR.2q7Ofpqp8NPFuHWTW3F_Q9wfW2Zphwt5uuDRYg", GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES).addEventListeners(new VCManager(), new CommandHelper()).build();
    }
}