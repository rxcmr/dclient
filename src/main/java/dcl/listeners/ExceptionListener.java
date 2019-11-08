package dcl.listeners;

import dcl.Skeleton;
import dcl.commands.utils.DirectMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class ExceptionListener extends ListenerAdapter {
   private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
      c == null ? d -> d.sendMessage(a).queue() : d -> d.sendMessage(a + c).queue()
   );

   @Override
   public void onException(@NotNull ExceptionEvent event) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      assert owner != null;
      dm.send("```java\n", owner, String.format("%s\n```", event.getCause()));
   }
}
