package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.music.Loader;

/**
 * @author rxcmr
 */
public class SkipTrackCommand extends Command {
   private final Loader loader;
   public SkipTrackCommand() {
      this.name = "skip";
      this.help = "Skips current playing track.";
      loader = new Loader();
   }

   @Override
   protected void execute(CommandEvent event) {
      event.getChannel().sendTyping().queue();
      loader.skipTrack(event.getTextChannel());
   }
}
