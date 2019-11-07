package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import dcl.Skeleton;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author rxcmr
 */
public class FleshListener implements CommandListener {

   @Override
   public void onCommandException(@NotNull CommandEvent event, Command command, Throwable throwable) {
      DirectMessage dm = (a, b, c) -> Objects.requireNonNull(b).openPrivateChannel().queue(
         d -> d.sendMessage(a + c.toString()).queue()
      );

      event.getChannel().sendTyping().queue();
      event.getMessage().addReaction("\u274C").queue();
      if (command instanceof PurgeCommand) {
         event.reply(Skeleton.prefix + command.getName() + " " + command.getArguments() + " [1-100]");
      } else {
         event.reply(Skeleton.prefix + command.getName() + " " + command.getArguments());
      }
      dm.sendMessage("Exception: ", event.getJDA().getUserById(Skeleton.ID), throwable);
   }

   @Override
   public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
      event.getMessage().addReaction("\u2705").queue();
   }
}
