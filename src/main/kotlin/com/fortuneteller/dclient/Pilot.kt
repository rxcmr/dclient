package com.fortuneteller.dclient

import com.fortuneteller.dclient.utils.EnvLoader
import com.jagrosh.jdautilities.command.Command
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Instant
import java.util.*

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
class Pilot(private val token: String, private val prefix: String, private val shards: Int) : Runnable {
  companion object {
    val initTime: Instant = Instant.now()
  }

  private val commands = LinkedList<Command>().apply {
    for (c in ClassGraph()
      .blacklistPackages(
        "com.fortuneteller.dclient.commands.utils",
        "com.fortuneteller.dclient.commands.gadgets.utils",
        "com.fortuneteller.dclient.commands.music.utils",
        "com.fortuneteller.dclient.commands.music.children",
        "com.fortuneteller.dclient.commands.statistics.children")
      .whitelistPackages("com.fortuneteller.dclient.commands.*")
      .scan()
      .getSubclasses(Command::class.java.name)
      .loadClasses(Command::class.java)) add(c.getDeclaredConstructor().newInstance())
  }

  private val listeners = LinkedList<Any>().apply {
    for (l in ClassGraph()
      .whitelistPackagesNonRecursive("com.fortuneteller.dclient.listeners")
      .scan()
      .getSubclasses(ListenerAdapter::class.java.name)
      .loadClasses(ListenerAdapter::class.java)) add(l.getDeclaredConstructor().newInstance())
  }

  override fun run() = Contraption(token, prefix, shards, commands, listeners).start()
}

fun main() = Pilot(EnvLoader.load("TOKEN"), "pl.", 1).run()