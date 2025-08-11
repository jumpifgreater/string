package bot.string;

import bot.string.handler.impl.vc.VCManager;
import bot.string.listeners.MessageDeleteListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {
    public static void main(String[] args) {
       JDABuilder.createDefault("MTM5ODM3NTc5MDc0MzM5MjI2Ng.Gr3pWN.oBQEPA6Jb2K4fcOhDEgH8xWdM4Jj_x5GaLc1_E", GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES)
       .addEventListeners(
               new VCManager(),
               new CommandManager(),
               new MessageDeleteListener()
       ).build();
    }
}