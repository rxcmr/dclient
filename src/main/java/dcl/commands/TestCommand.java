package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author rxcmr
 */
public class TestCommand extends Command {
   public TestCommand() {
      this.name = "test";
      this.aliases = new String[]{"try"};
      this.help = "Testing command handler of JDA-Utilities";
      this.ownerCommand = true;
      this.category = new Category("Owner");
      this.hidden = true;
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      String[] args = event.getArgs().split("\\s+");
      event.getChannel().sendTyping().queue();
      Arrays.stream(args).forEachOrdered(event::reply);
   }
}
