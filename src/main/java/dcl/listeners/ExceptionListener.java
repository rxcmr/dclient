package dcl.listeners;

import dcl.Skeleton;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExceptionListener extends ListenerAdapter {
   @Override
   public void onException(@NotNull ExceptionEvent event) {
      User owner = Objects.requireNonNull(event.getJDA().getUserById(Skeleton.ID));
      sendDirectMessage(owner, "An exception has occurred.");
      sendDirectMessage(owner, event.getCause().getMessage());
   }

   private void sendDirectMessage(@NotNull User user, @NotNull String message) {
      user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
   }
}
