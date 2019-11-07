package dcl.listeners;

import ch.qos.logback.classic.Logger;
import dcl.Skeleton;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author rxcmr
 */
public class ReconnectListener extends ListenerAdapter {
   @Override
   public void onReconnect(@Nonnull ReconnectedEvent event) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      Logger logger = (Logger) LoggerFactory.getLogger(ReconnectListener.class);
      logger.info("Reconnected!");
      logger.info("REST HTTP Ping: " + event.getJDA().getRestPing().complete());
      logger.info("WebSocket Ping: " + event.getJDA().getGatewayPing());
      assert owner != null;
      sendDirectMessage(owner);
   }

   private void sendDirectMessage(@NotNull User user) {
      user.openPrivateChannel().queue(channel -> channel.sendMessage("Reconnected!").queue());
   }
}
