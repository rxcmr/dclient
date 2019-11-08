package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import groovy.lang.GroovyShell;

/**
 * @author rxcmr
 */
public class DebugCommand extends Command {
   private final GroovyShell shell;
   private final String libs;

   public DebugCommand() {
      this.name = "debug";
      this.aliases = new String[]{"eval"};
      this.ownerCommand = true;
      this.help = "JDA evaluator using Groovy";
      this.hidden = true;
      this.category = new Category("Owner");
      shell = new GroovyShell();
      libs = "import java.io.*\n" +
         "import java.lang.*\n" +
         "import java.util.*\n" +
         "import java.util.concurrent.*\n" +
         "import net.dv8tion.jda.core.*\n" +
         "import net.dv8tion.jda.core.entities.*\n" +
         "import net.dv8tion.jda.core.entities.impl.*\n" +
         "import net.dv8tion.jda.core.managers.*\n" +
         "import net.dv8tion.jda.core.managers.impl.*\n" +
         "import net.dv8tion.jda.core.utils.*\n";
   }

   @Override
   protected void execute(CommandEvent event) {
      try {
         shell.setProperty("args", event.getArgs());
         shell.setProperty("event", event);
         shell.setProperty("message", event.getMessage());
         shell.setProperty("channel", event.getChannel());
         shell.setProperty("jda", event.getJDA());
         shell.setProperty("guild", event.getGuild());
         shell.setProperty("member", event.getMember());
         shell.setProperty("user", event.getMember().getUser());

         String script = libs + event.getMessage().getContentRaw().split("\\s+", 2)[1];
         Object out = shell.evaluate(script);

         event.reply(out == null ? "```Finished execution.```" : String.format("```%s```", out.toString()));
      } catch (Exception e) {
         event.reply("```java\n" + e + " \ncause: " + (e.getCause() == null ? "nothing" : e.getCause()) + "\n```");
      }
   }
}
