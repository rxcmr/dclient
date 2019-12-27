package com.fortuneteller.dclient.commands.owner

import com.fortuneteller.dclient.commands.utils.Categories
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.util.*
import java.util.stream.Collectors

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

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class GhostMessageCommand : Command() {
  override fun execute(event: CommandEvent) {
    with(event) {
      val args = args.split("\\s+".toRegex()).toTypedArray()
      val channel = Objects.requireNonNull(jda.getGuildById(args[0]))!!.getTextChannelById(args[1])
      val message = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
      channel?.sendTyping()?.queue()
      if (channel?.canTalk()!!) channel.sendMessage(message).queue()
      else reply("Lacking `MESSAGE_WRITE` permissions.")
    }
  }

  init {
    name = "ghost"
    help = "Sends a message to a server remotely."
    arguments = "**<guildID>** **<channelID>** **<message>**"
    hidden = true
    ownerCommand = true
    category = Categories.OWNER.category
  }
}