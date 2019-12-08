package dcl.commands;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *
 * dclient, a JDA Discord bot
 *      Copyright (C) 2019 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class LatencyCommand extends Command {
  public MessageEmbed embed;

  public LatencyCommand() {
    name = "latency";
    aliases = new String[]{"ping"};
    help = "REST API ping and WebSocket ping.";
    guildOnly = false;
    ownerCommand = true;
    category = Categories.OWNER.getCategory();
    hidden = true;
  }

  public synchronized void buildEmbed(@NotNull CommandEvent event) {
    JDA jda = event.getJDA();
    EmbedBuilder embedBuilder = new EmbedBuilder();
    jda.getRestPing().queue(api -> embed = embedBuilder
      .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
      .addField("**API: **", "```py\n" + api + " ms\n```", true)
      .addField("**WebSocket: **", "```py\n" + jda.getGatewayPing() + " ms\n```", true)
      .setColor(0xd32ce6)
      .build()
    );
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue(
      v -> {
        buildEmbed(event);
        Executors.newScheduledThreadPool(1).schedule(
          () -> event.reply(embed), 500, TimeUnit.MILLISECONDS
        );
      }
    );
  }
}
