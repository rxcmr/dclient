package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.utils.Categories
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.util.concurrent.TimeUnit

/*
 * Copyright 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *      Copyright (C) 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
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

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class PingCommand : Command() {
  private lateinit var embed: MessageEmbed

  @Synchronized
  fun buildEmbed(event: CommandEvent) = event.jda.restPing.queue { api ->
    embed = EmbedBuilder()
      .setThumbnail(event.author.effectiveAvatarUrl)
      .addField("**API: **", "```py\n$api ms\n```", true)
      .addField("**WebSocket: **", "```py\n${event.jda.gatewayPing} ms\n```", true)
      .setColor(0xd32ce6)
      .build()
  }

  override fun execute(event: CommandEvent) {
    event.channel.sendTyping().queue()
    buildEmbed(event)
    event.jda.gatewayPool.schedule({ event.reply(embed) }, 1, TimeUnit.SECONDS)
  }

  init {
    name = "latency"
    aliases = arrayOf("ping")
    help = "REST API ping and WebSocket ping."
    category = Categories.GADGETS.category
  }
}