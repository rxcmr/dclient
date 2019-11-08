package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class LeaveVoiceChannelCommand extends Command {
   public LeaveVoiceChannelCommand() {
      this.name = "leave";
      this.botPermissions = new Permission[]{Permission.VOICE_CONNECT};
      this.help = "Leaves the voice channel.";
      this.category = new Category("Music");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getGuild().kickVoiceMember(event.getSelfMember()).queue();
   }
}
