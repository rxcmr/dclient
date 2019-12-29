package com.fortuneteller.dclient.listeners

import com.fortuneteller.dclient.Contraption
import com.fortuneteller.dclient.utils.PilotUtils
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
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
@SuppressWarnings("unused")
class ReadyEventListener : ListenerAdapter() {
  override fun onReady(event: ReadyEvent) = event.jda.let { j ->
    val esc = Contraption.esc
    val shardInfo = j.shardInfo
    val shards = "$esc[1;91m[${shardInfo.shardId + 1}/${shardInfo.shardTotal}]$esc[0m"
    val inviteURL = j.getInviteUrl(
      Permission.BAN_MEMBERS,
      Permission.KICK_MEMBERS,
      Permission.MESSAGE_MANAGE,
      Permission.MANAGE_ROLES,
      Permission.MANAGE_SERVER
    )
    val guilds = j.guilds.stream().map { obj -> obj.name }.collect(Collectors.joining(", "))
    j.restPing.queue { api ->
      PilotUtils.info("|$esc[1;92m       R U N N I N G        $esc[0m| Status: $esc[1;92m${j.status}$esc[0m")
      PilotUtils.info("|                            | Logged in as: ${j.selfUser.asTag}")
      PilotUtils.info("|$esc[1;95m       ██╗██████╗  █████╗   $esc[0m| Guilds available: ${event.guildAvailableCount}")
      PilotUtils.info("|$esc[1;95m       ██║██╔══██╗██╔══██╗  $esc[0m| Owner ID: ${Contraption.ID}")
      PilotUtils.info("|$esc[1;95m  ██   ██║██║  ██║██╔══██║  $esc[0m| Guilds: $guilds")
      PilotUtils.info("|$esc[1;95m  ╚█████╔╝██████╔╝██║  ██║  $esc[0m| Shard ID: ${shardInfo.shardId}")
      PilotUtils.info("|$esc[1;95m   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  $esc[0m| Invite URL: $inviteURL")
      PilotUtils.info("|                            | Account type: ${j.accountType}")
      PilotUtils.info("|$esc[1;92m    [version   ${JDAInfo.VERSION}]    $esc[0m| WebSocket Ping: ${j.gatewayPing}")
      PilotUtils.info("|$esc[1;92m    [dcl version ${Contraption.VERSION}]    $esc[0m| API Ping: $api")
      PilotUtils.info("|                            | Shards: $shards")
    }
    PilotUtils.gc()
  }
}