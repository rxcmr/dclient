package dcl.commands;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
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
      this.category = Categories.ownerOnly;
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
