package com.fortuneteller.dclient.commands.moderation

import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.utils.ExMessage
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel

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
class SlowmodeCommand : Command() {
  override fun execute(event: CommandEvent) = try {
    if (event.args.toInt() >= 0 && event.args.toInt() <= TextChannel.MAX_SLOWMODE)
      event.textChannel.manager.setSlowmode(event.args.toInt()).queue()
    else throw CommandException(ExMessage.SLOWMODE_DURATION)
  } catch (e: NumberFormatException) {
    throw CommandException(ExMessage.INVALID_INTEGER)
  }

  init {
    name = "slowmode"
    aliases = arrayOf("slow")
    help = "Toggles slow mode for the current channel."
    arguments = "**<seconds>**"
    botPermissions = arrayOf(Permission.MESSAGE_MANAGE, Permission.MANAGE_SERVER, Permission.MANAGE_CHANNEL)
    userPermissions = arrayOf(Permission.MESSAGE_MANAGE, Permission.MANAGE_SERVER, Permission.MANAGE_CHANNEL)
    category = Categories.MODERATION.category
  }
}