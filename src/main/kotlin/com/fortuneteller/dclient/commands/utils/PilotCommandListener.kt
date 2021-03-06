package com.fortuneteller.dclient.commands.utils

import com.fortuneteller.dclient.Contraption
import com.fortuneteller.dclient.commands.music.children.LeaveCommand
import com.fortuneteller.dclient.commands.music.children.PlayCommand
import com.fortuneteller.dclient.commands.music.children.SearchCommand
import com.fortuneteller.dclient.commands.owner.TestCommand
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import com.jagrosh.jdautilities.examples.command.PingCommand
import com.jagrosh.jdautilities.examples.command.ShutdownCommand

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
class PilotCommandListener : CommandListener {
  override fun onCommandException(event: CommandEvent, command: Command, throwable: Throwable) = with(event) {
    channel?.sendTyping()?.queue()
    command.let {
      throwable.let { t ->
        when (it) {
          is PingCommand -> reply("Request didn't go through.")
          is TestCommand -> reply("```kotlin\nTest complete.\nThrew:\n$t```")
          is PlayCommand -> SearchCommand().execute(this)
          else -> {
            message?.addReaction("\uD83D\uDE41")?.queue()
            replyToOwner("```java\n$t\n```")
            reply(when (it.arguments) {
              null -> "Something wrong happened..."
              else -> "${Contraption.prefix}${it.name} ${it.arguments}"
            })
            reply(t.message)
          }
        }
      }
    }
  }

  override fun onCompletedCommand(event: CommandEvent, command: Command) {
    if (command is ShutdownCommand) return
    else event.message?.addReaction("\uD83D\uDE42")?.queue()
  }

  override fun onTerminatedCommand(event: CommandEvent, command: Command) = with(event) {
    if (command is LeaveCommand) return
    message?.addReaction("\uD83E\uDD2C")?.queue()
    channel?.sendTyping()?.queue()
    reply("Unexpected behavior. Try again.")
    replyToOwner("Unexpected behavior.\n" +
        "Triggered by: ${author.name} in ${guild.name} in ${channel.name}\n Input: $message")
  }
}