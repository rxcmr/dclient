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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class LatencyCommand extends Command {
  public LatencyCommand() {
    name = "latency";
    aliases = new String[]{"ping"};
    help = "REST API ping and WebSocket ping.";
    guildOnly = false;
    ownerCommand = true;
    category = Categories.Owner;
    hidden = true;
  }

  @NotNull
  public synchronized MessageEmbed buildEmbed(@NotNull CommandEvent event) {
    JDA jda = event.getJDA();
    EmbedBuilder embedBuilder = new EmbedBuilder();
    return embedBuilder
      .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
      .addField("**API: **", "```py\n" + jda.getRestPing().complete() + " ms\n```", true)
      .addField("**WebSocket: **", "```py\n" + jda.getGatewayPing() + " ms\n```", true)
      .setColor(0xd32ce6)
      .build();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue();
    event.reply(buildEmbed(event));
  }
}
