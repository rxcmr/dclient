package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

/**
 * @author rxcmr
 */
public class KickCommand extends Command {
   public KickCommand() {
      this.name = "kick";
      this.arguments = "**user**";
      this.help = "Kicks a user";
      this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
      this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
      this.category = new Category("Moderation");
   }

   @Override
   protected void execute(CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.getMessage().getMentionedMembers().get(0).kick().queue();
   }
}
