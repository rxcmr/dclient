package com.fortuneteller.dclient.commands.music

import com.fortuneteller.dclient.commands.utils.Categories
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.Permission
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

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
@Suppress("unused")
class MusicCommand : Command() {
  override fun execute(event: CommandEvent) {
    event.reply("""
      play - plays URL
      queue - shows tracks queued
      skip - skips track
      leave - leave VC
      search - search YouTube and play
      pause - pauses track
      resume - resumes track
      stop - stops track
      repeat - repeats track
      shuffle - shuffles track list
      reset - resets player
    """.trimIndent())
  }

  init {
    name = "music"
    aliases = arrayOf("m")
    children = ArrayList<Command>().let {
      for (mc in ClassGraph()
        .whitelistPackagesNonRecursive("com.fortuneteller.dclient.commands.music.children")
        .scan()
        .getSubclasses(Command::class.java.name)
        .loadClasses(Command::class.java)) it.add(mc.getDeclaredConstructor().newInstance())
      it.toTypedArray()
    }
    help = Arrays.stream(children).map { c ->
      "\n   - " + (if (c.arguments != null) c.arguments + " " else "") + c.help
    }.collect(Collectors.joining("", "General music commands.", ""))
    arguments = "**<command>**"
    botPermissions = arrayOf(
      Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.PRIORITY_SPEAKER
    )
    category = Categories.MUSIC.category
  }
}