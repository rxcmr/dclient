package com.fortuneteller.dclient

import com.fortuneteller.dclient.commands.gadgets.*
import com.fortuneteller.dclient.commands.moderation.*
import com.fortuneteller.dclient.commands.music.MusicCommand
import com.fortuneteller.dclient.commands.owner.*
import com.fortuneteller.dclient.listeners.GuildJoinListener
import com.fortuneteller.dclient.listeners.ReadyEventListener
import com.fortuneteller.dclient.listeners.ShutdownListener
import io.github.cdimascio.dotenv.Dotenv

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

class Pilot(token: String?, prefix: String?, shards: Int) {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val mainToken = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load()["TOKEN"]
      val mainPrefix = "pl."
      val shards = 1
      Pilot(mainToken, mainPrefix, shards)
    }
  }

  init {
    //val commands = LinkedList<Command>()
    //val listeners = LinkedList<Any>()

    /*
    for (c in ClassGraph()
      .blacklistPackages(
        "com.fortuneteller.dclient.commands.utils",
        "com.fortuneteller.dclient.commands.gadgets.utils",
        "com.fortuneteller.dclient.commands.music.utils",
        "com.fortuneteller.dclient.commands.music.children"
      )
      .whitelistPackages("com.fortuneteller.dclient.commands.*")
      .verbose()
      .scan()
      .allClasses
      .loadClasses(Command::class.java)) commands.add(c.getDeclaredConstructor().newInstance())


    for (l in ClassGraph()
      .whitelistPackagesNonRecursive("com.fortuneteller.dclient.listeners")
      .scan()
      .allClasses
      .loadClasses(ListenerAdapter::class.java)) listeners.add(l.getDeclaredConstructor().newInstance())
     */

    val commands = listOf(
      GoogleSearchCommand(), JagTagCommand(), JavadocCommand(), PingCommand(), PurgeCommand(), QueryUserCommand(),
      ShardCommand(), UptimeCommand(), BanCommand(), KickCommand(), MuteCommand(), SlowmodeCommand(), SoftbanCommand(),
      MusicCommand(), CustomQueryCommand(), EvalCommand(), GarbageCollectionCommand(), GhostMessageCommand(),
      LoadCommand(), PauseThreadCommand(), ShutdownCommand(), TestCommand(), UnloadCommand()
    )

    val listeners = listOf(GuildJoinListener(), ReadyEventListener(), ShutdownListener())

    Contraption(token!!, prefix!!, shards, commands, listeners).start()
  }
}