package dcl.listeners;

import ch.qos.logback.classic.Logger;
import dcl.Skeleton;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class DisconnectListener extends ListenerAdapter {
   @Override
   public void onDisconnect(@NotNull DisconnectEvent event) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      Logger logger = (Logger) LoggerFactory.getLogger(DisconnectListener.class);
      logger.warn("Disconnected.");
      logger.info("Attempting to reconnect.");
      assert owner != null;
      sendDirectMessage(owner);
   }

   private void sendDirectMessage(@NotNull User user) {
      user.openPrivateChannel().queue(channel -> channel.sendMessage("Disconnected!").queue());
   }
}
