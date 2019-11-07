package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
public class ShardCommand extends Command {
   public ShardCommand() {
      this.name = "shardinfo";
      this.aliases = new String[]{"shards"};
      this.help = "Sharding info.";
      this.ownerCommand = true;
      this.category = new Category("Owner");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.reply("Shards: " + event.getJDA().getShardInfo().getShardString());
   }
}
