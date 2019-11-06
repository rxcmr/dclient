package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

public class LatencyCommand extends Command {
   public LatencyCommand() {
      this.name = "latency";
      this.aliases = new String[]{"ping"};
      this.help = "REST HTTP ping and WebSocket ping.";
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.getJDA().getRestPing().queue(
         ping ->
            event
               .getChannel()
               .sendMessageFormat(
                  "Ping%nHTTP: %d ms%nWebSocket: %d ms",
                  ping,
                  event.getJDA().getGatewayPing()
               )
               .queue()
      );
   }
}
