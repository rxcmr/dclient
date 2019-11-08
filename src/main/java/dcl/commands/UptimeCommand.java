package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class UptimeCommand extends Command {
   public UptimeCommand() {
      this.name = "uptime";
      this.help = "Bot uptime.";
      this.ownerCommand = true;
      this.hidden = true;
      this.category = new Category("Owner");
   }

   @Override
   protected void execute(CommandEvent event) {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      long uptime = runtimeMXBean.getUptime();
      long uptimeInSeconds = uptime / 1000;
      long numberOfHours = uptimeInSeconds / (60 * 60);
      long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
      long numberOfSeconds = uptimeInSeconds % 60;

      event.getChannel().sendMessageFormat(
         "`%s:%s:%s`", new Object[]{numberOfHours, numberOfMinutes, numberOfSeconds}
         ).queue();
   }
}
