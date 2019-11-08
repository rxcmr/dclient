package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class LatencyCommand extends Command {
   private EmbedBuilder embedBuilder = new EmbedBuilder();

   public LatencyCommand() {
      this.name = "latency";
      this.aliases = new String[]{"ping"};
      this.help = "REST API ping and WebSocket ping.";
      this.guildOnly = false;
      this.ownerCommand = true;
      this.category = new Category("Owner");
      this.hidden = true;
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.reply(buildEmbed(event.getAuthor(), event));
      embedBuilder.clear();
   }

   @NotNull
   private MessageEmbed buildEmbed(@NotNull User user, @NotNull CommandEvent event) {
      event.getJDA().getRestPing().queue(p -> embedBuilder.addField("API: ", p + " ms", true));
      embedBuilder
         .setThumbnail(user.getEffectiveAvatarUrl())
         .addField("WebSocket: ", event.getJDA().getGatewayPing() + " ms", true)
         .setColor(0xd32ce6);
      return embedBuilder.build();
   }
}
