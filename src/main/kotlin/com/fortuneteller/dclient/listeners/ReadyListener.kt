package com.fortuneteller.dclient.listeners

import com.fortuneteller.dclient.Contraption
import com.fortuneteller.dclient.Pilot
import com.fortuneteller.dclient.utils.Colors.BLUE_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.GREEN_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.PURPLE_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.RED_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.RESET
import com.fortuneteller.dclient.utils.Colors.YELLOW_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.PilotUtils.gc
import com.fortuneteller.dclient.utils.PilotUtils.info
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors

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
class ReadyListener : ListenerAdapter() {
  override fun onReady(event: ReadyEvent) = event.jda.let { j ->
    val shardInfo = j.shardInfo
    val shards = "$RED_BOLD_BRIGHT[${shardInfo.shardId + 1}/${shardInfo.shardTotal}]$RESET"
    val inviteURL = j.getInviteUrl(
      Permission.BAN_MEMBERS,
      Permission.KICK_MEMBERS,
      Permission.MESSAGE_MANAGE,
      Permission.MANAGE_ROLES,
      Permission.MANAGE_SERVER
    )
    val guilds = j.guilds.stream().map { g -> g.name }.collect(Collectors.joining(", "))
    j.restPing.queue { api ->
      info("|$GREEN_BOLD_BRIGHT        R U N N I N G       $RESET| Status: $GREEN_BOLD_BRIGHT${j.status}$RESET")
      info("|                            | Logged in as: $BLUE_BOLD_BRIGHT${j.selfUser.asTag}$RESET")
      info("|$PURPLE_BOLD_BRIGHT       ██╗██████╗  █████╗   $RESET| Guilds available: ${event.guildAvailableCount}")
      info("|$PURPLE_BOLD_BRIGHT       ██║██╔══██╗██╔══██╗  $RESET| Owner ID: ${Contraption.ID}")
      info("|$PURPLE_BOLD_BRIGHT  ██   ██║██║  ██║██╔══██║  $RESET| Guilds: $guilds")
      info("|$PURPLE_BOLD_BRIGHT  ╚█████╔╝██████╔╝██║  ██║  $RESET| Shard ID: ${shardInfo.shardId}")
      info("|$PURPLE_BOLD_BRIGHT   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  $RESET| Invite URL: $inviteURL")
      info("|                            | Account type: ${j.accountType}")
      info("|$GREEN_BOLD_BRIGHT    [version  ${JDAInfo.VERSION}]    $RESET| " +
        "WebSocket Ping: ${ when {
          j.gatewayPing <= 125 -> "$GREEN_BOLD_BRIGHT${j.gatewayPing}$RESET"
          j.gatewayPing <= 325 -> "$YELLOW_BOLD_BRIGHT${j.gatewayPing}$RESET"
          else -> "$RED_BOLD_BRIGHT$api$RESET"
        }}")
      info("|$GREEN_BOLD_BRIGHT    [dcl version ${Contraption.VERSION}]    $RESET| " +
        "API Ping: ${ when {
          api <= 125 -> "$GREEN_BOLD_BRIGHT$api$RESET"
          api <= 325 -> "$YELLOW_BOLD_BRIGHT$api$RESET"
          else -> "$RED_BOLD_BRIGHT$api$RESET"
        }}")
      info("|                            | Shards: $shards")
      info("Finished ready for Shard ${shardInfo.shardId} in " +
        "${Duration.between(Pilot.initTime, Instant.now()).toMillis()} ms")
    }
    gc()
  }
}