package dcl.listeners;

import ch.qos.logback.classic.Logger;
import dcl.Skeleton;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public class ReadyEventListener extends ListenerAdapter {
   private Logger logger = (Logger) LoggerFactory.getLogger(ReadyEventListener.class);
   @Override
   public void onReady(@NotNull ReadyEvent event) {
      logger.info("|       R U N N I N G        | Status: " + event.getJDA().getStatus());
      logger.info("|                            | Logged in as: " + event.getJDA().getSelfUser().getAsTag());
      logger.info("|       ██╗██████╗  █████╗   | Guilds available: " + event.getGuildAvailableCount());
      logger.info("|       ██║██╔══██╗██╔══██╗  | REST HTTP Ping: " + event.getJDA().getRestPing().complete());
      logger.info("|  ██   ██║██║  ██║██╔══██║  | WebSocket Ping: " + event.getJDA().getGatewayPing());
      logger.info("|  ╚█████╔╝██████╔╝██║  ██║  | Sharding: " + event.getJDA().getShardInfo().getShardString());
      logger.info("|   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  | Invite URL: " + event.getJDA().getInviteUrl());
      logger.info("|                            | Account type: " + event.getJDA().getAccountType());
      logger.info("|     [version 4.0.0_56]     | Guilds: " + event.getJDA().getGuilds());
      logger.info("|                            | Owner ID: " + Skeleton.ID);
   }
}
