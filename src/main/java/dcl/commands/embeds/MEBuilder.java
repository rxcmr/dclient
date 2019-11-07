package dcl.commands.embeds;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * @author rxcmr
 */
public interface MEBuilder {
   MessageEmbed buildEmbed(Member member, User user);
}
