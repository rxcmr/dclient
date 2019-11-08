package dcl.commands.listener;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import dcl.Skeleton;
import dcl.commands.ShutdownCommand;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
interface DirectMessage {
   void send(String content, User user, Object optional);
}

public class FleshListener implements CommandListener {
   DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(d -> d.sendMessage(a + c.toString()).queue());

   @Override
   public void onCommandException(@NotNull CommandEvent event, Command command, Throwable throwable) {
      event.getChannel().sendTyping().queue();
      event.getMessage().addReaction("\u274C").queue();
      event.reply(Skeleton.prefix + command.getName() + " " + command.getArguments());
      dm.send("Exception: ", event.getJDA().getUserById(Skeleton.ID), throwable.getCause());
   }

   @Override
   public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
      if (command instanceof ShutdownCommand) return;
      event.getMessage().addReaction("\u2705").queue();
   }

   @Override
   public void onTerminatedCommand(@NotNull CommandEvent event, Command command) {
      event.getMessage().addReaction("\u274C").queue();
      event.getChannel().sendTyping().queue();
      event.reply("Unexpected behavior?");
      dm.send(
         "Unexpected behavior. Triggered by: ",
         event.getJDA().getUserById(Skeleton.ID),
         event.getAuthor() + " in " + event.getGuild()
      );
   }
}
