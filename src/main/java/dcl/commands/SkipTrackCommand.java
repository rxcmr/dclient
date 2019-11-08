package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.music.Loader;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class SkipTrackCommand extends Command {
   private final Loader loader;
   public SkipTrackCommand() {
      this.name = "skip";
      this.help = "Skips current playing track.";
      this.category = new Category("Music");
      loader = new Loader();
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      loader.skipTrack(event.getTextChannel());
   }
}
