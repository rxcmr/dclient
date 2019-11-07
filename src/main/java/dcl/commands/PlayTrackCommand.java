package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.music.Loader;
import net.dv8tion.jda.api.Permission;

/**
 * @author rxcmr
 */
public class PlayTrackCommand extends Command {
   private final Loader loader;

   public PlayTrackCommand() {
      this.name = "play";
      this.arguments = "**URL**";
      this.botPermissions = new Permission[]{Permission.PRIORITY_SPEAKER, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT};
      this.help = "Plays a track from URL.";
      loader = new Loader();
   }

   @Override
   protected void execute(CommandEvent event) {
      event.getChannel().sendTyping().queue();
      loader.loadAndPlay(event.getTextChannel(), event.getArgs());
   }
}
