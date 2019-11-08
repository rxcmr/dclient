package dcl.commands.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import dcl.Skeleton;
import dcl.commands.ShutdownCommand;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author rxcmr
 */
public class FleshListener implements CommandListener {
   private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
      c == null ? d -> d.sendMessage(a).queue() : d -> d.sendMessage(a + c).queue()
   );

   @Override
   public void onCommandException(@NotNull CommandEvent event, @NotNull Command command, @NotNull Throwable throwable) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      event.getChannel().sendTyping().queue();
      event.getMessage().addReaction("\u274C").queue();
      event.reply(
         command.getArguments() == null ?
            "Something wrong happened..." : Skeleton.prefix + command.getName() + " " + command.getArguments()
      );
      assert owner != null;
      dm.send("```java\n", owner, String.format("%s\n```", throwable));
   }

   @Override
   public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
      if (command instanceof ShutdownCommand) return;
      event.getMessage().addReaction("\u2705").queue();
   }

   @Override
   public void onTerminatedCommand(@NotNull CommandEvent event, Command command) {
      User owner = Objects.requireNonNull(event.getJDA().getUserById(Skeleton.ID));
      event.getMessage().addReaction("\u274C").queue();
      event.getChannel().sendTyping().queue();
      event.reply("Unexpected behavior. Try again.");
      dm.send(
         "Unexpected behavior. Triggered by: ",
         owner,
         event.getAuthor() + " in " + event.getGuild()
      );
   }
}
