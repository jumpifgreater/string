import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.entities.channel.concrete.VoiceChannelImpl;
import net.dv8tion.jda.internal.handle.ApplicationCommandPermissionsUpdateHandler;

public class Bot {
    public static void main(String[] args) {
        JDABuilder.createDefault("token", GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES).addEventListeners(new VCManager(), new CommandHelper()).build(); // bug
    }
}