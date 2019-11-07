package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
public class TestCommand extends Command {
   public TestCommand() {
      this.name = "test";
      this.aliases = new String[]{"try"};
      this.help = "Testing command handler of JDA-Utilities";
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.reply("it works bro");
   }
}
