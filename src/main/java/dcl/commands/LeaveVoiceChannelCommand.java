package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

/**
 * @author rxcmr
 */
public class LeaveVoiceChannelCommand extends Command {
   public LeaveVoiceChannelCommand() {
      this.name = "leave";
      this.botPermissions = new Permission[]{Permission.VOICE_CONNECT};
      this.help = "Leaves the voice channel.";
   }

   @Override
   protected void execute(CommandEvent event) {
      event.getGuild().kickVoiceMember(event.getSelfMember()).queue();
   }
}
