import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Widget;

public class Utils {
    public static Member getMentionedMember(String input, Guild guild) {
        if (input.matches("<@!?(\\d+)>")) input = input.replaceAll("\\D+", ""); // bug
        try {
            return guild.retrieveMemberById(input).complete();
        } catch (Exception e) {
            return null;
        }
    }
}

