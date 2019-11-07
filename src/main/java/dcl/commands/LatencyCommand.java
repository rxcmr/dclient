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
public class LatencyCommand extends Command {
   private EmbedBuilder embedBuilder = new EmbedBuilder();

   public LatencyCommand() {
      this.name = "latency";
      this.aliases = new String[]{"ping"};
      this.help = "REST HTTP ping and WebSocket ping.";
      this.guildOnly = false;
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      event.reply(buildEmbed(event.getAuthor(), event));
      clearEmbed();
   }

   @NotNull
   private MessageEmbed buildEmbed(@NotNull User user, @NotNull CommandEvent event) {
      embedBuilder
         .setThumbnail(user.getEffectiveAvatarUrl())
         .addField("REST HTTP Ping: ", event.getJDA().getRestPing().complete() + " ms", true)
         .addField("WebSocket Ping: ", event.getJDA().getGatewayPing() + " ms", true)
         .setColor(0xd32ce6);
      return embedBuilder.build();
   }

   private void clearEmbed() { embedBuilder.clear(); }
}
