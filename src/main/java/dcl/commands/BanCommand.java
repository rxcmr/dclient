package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class BanCommand extends Command {
   public BanCommand() {
      this.name = "ban";
      this.arguments = "**user** **amount** (in days)";
      this.help = "Bans a user";
      this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
      this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
      this.category = new Category("Moderation");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      String[] args = event.getArgs().split("\\s+");
      event.getChannel().sendTyping().queue();
      event.getMessage().getMentionedMembers().get(0).ban(Integer.parseInt(args[1])).queue();
   }
}
