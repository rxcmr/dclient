package com.fortuneteller.dclient.commands.owner

import com.fortuneteller.dclient.Contraption.Companion.commandClient
import com.fortuneteller.dclient.Contraption.Companion.shardManager
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.reflect.InvocationTargetException

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
 */ /**
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
class LoadCommand : Command() {
  override fun execute(event: CommandEvent) {
    try {
      if (event.args.contains("Command")) {
        val command = ClassGraph()
          .whitelistPackages("com.fortuneteller.dclient.commands.*")
          .blacklistPackages("com.fortuneteller.dclient.commands.utils")
          .scan()
          .getSubclasses(Command::class.java.name)
          .filter { c -> c.simpleName == event.args }
          .loadClasses(Command::class.java)[0].getDeclaredConstructor().newInstance()
        commandClient.addCommand(command, commandClient.commands.size - 1)
      } else if (event.args.contains("Listener")) {
        val listener = ClassGraph()
          .whitelistPackages("com.fortuneteller.dcl.listeners")
          .scan()
          .getSubclasses(ListenerAdapter::class.java.name)
          .filter { c -> c.simpleName == event.args }
          .loadClasses(ListenerAdapter::class.java)[0].getDeclaredConstructor().newInstance()
        shardManager.addEventListener(listener)
      }
    } catch (e: IndexOutOfBoundsException) {
      throw CommandException(e.message)
    } catch (e: NoSuchMethodException) {
      throw CommandException(e.message)
    } catch (e: IllegalAccessException) {
      throw CommandException(e.message)
    } catch (e: InstantiationException) {
      throw CommandException(e.message)
    } catch (e: InvocationTargetException) {
      throw CommandException(e.message)
    }
  }

  init {
    name = "load"
    arguments = "**<class>**"
    help = "Loads a Class<Command>, or a Class<ListenerAdapter>"
    hidden = true
    ownerCommand = true
    category = Categories.OWNER.category
  }
}