package dcl.commands;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

/**
 * @author rxcmr
 */
public class ShutdownCommand extends Command {
   public ShutdownCommand() {
      this.name = "shutdown";
      this.aliases = new String[]{"shutdown"};
      this.help = "Don't even try, it will fail on you.";
      this.ownerCommand = true;
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      Logger logger = (Logger) LoggerFactory.getLogger(ShutdownCommand.class);
      logger.warn("[!!] JDA shutting down.");
      event.getJDA().shutdownNow();
      System.exit(1);
   }
}
