import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Widget;

public class Utils {
    public static Member getMentionedMember(String mention, Guild guild) {
        if (mention.startsWith("<@") && mention.endsWith(">")) {
            mention = mention.replaceAll("[<@!>]", "");
            return  guild.getMemberById(mention);
        }

        return null;
    }
}
