package bot.string.handler.impl.fun;

import bot.string.handler.CommandHandler;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


//TODO: WORK
public class TTSHandler implements CommandHandler {
    private static VoiceManager voiceManager = VoiceManager.getInstance();

    @Override
    public void handle(String[] args, Member author, Guild guild, TextChannel channel) {
        if (args.length < 2) {
            channel.sendMessage("Usage: ,tts \"msg\"").queue();
            return;
        }

        var fullMessage = String.join(" ", args);
        int firstQuote = fullMessage.indexOf("\"");
        int lastQuote = fullMessage.lastIndexOf("\"");

        if (firstQuote == -1 || lastQuote == -1 || firstQuote == lastQuote) {
            channel.sendMessage("msg is not inside the quotes").queue();
            return;
        }

        var finalMsg = fullMessage.substring(firstQuote + 1, lastQuote);

        var name = "msg";

        var audioPlayer = new SingleFileAudioPlayer(name, AudioFileFormat.Type.WAVE);

        var voices = getVoices();
        StringBuilder sb = new StringBuilder("Available voices:\n");
        for (String voiceName : voices) {
            sb.append(" - ").append(voiceName).append("\n");
        }
        System.out.println(sb.toString());
/*        voice.allocate();
        voice.setAudioPlayer(audioPlayer);

        voice.speak(finalMsg);

        audioPlayer.close();
        voice.deallocate();

        var wavFile = new File(name + ".wav");
        if (wavFile.exists())
            channel.sendFiles(FileUpload.fromData(wavFile)).queue();
        else
            channel.sendMessage("failed to generate audio").queue();*/
    }

    public static List<String> getVoices() {
        System.getProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        var voices = new ArrayList<String>();
        for (var v : voiceManager.getVoices())
            voices.add(v.getName());

        return voices;
    }
}
