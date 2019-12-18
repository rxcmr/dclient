package com.fortuneteller.dcl.listeners;

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

import com.fortuneteller.dcl.Contraption;
import com.fortuneteller.dcl.utils.PilotUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.fortuneteller.dcl.utils.PilotUtils.info;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class ReadyEventListener extends ListenerAdapter {
  @Override
  public void onReady(@NotNull ReadyEvent event) {
    JDA jda = event.getJDA();
    JDA.ShardInfo shardInfo = jda.getShardInfo();
    String shards = String.format("\033[1;91m[%s/%s]\033[0m", shardInfo.getShardId() + 1, shardInfo.getShardTotal());
    String inviteURL = jda.getInviteUrl(
      Permission.BAN_MEMBERS,
      Permission.KICK_MEMBERS,
      Permission.MESSAGE_MANAGE,
      Permission.MANAGE_ROLES,
      Permission.MANAGE_SERVER
    );
    String guilds = jda.getGuilds().stream().map(Guild::getName).collect(Collectors.joining(", "));
    jda.getRestPing().queue(api -> {
      info("|\033[1;92m       R U N N I N G        \033[0m| Status: \033[1;92m" + jda.getStatus() + "\033[0m");
      info("|                            | Logged in as: " + jda.getSelfUser().getAsTag());
      info("|\033[1;95m       ██╗██████╗  █████╗   \033[0m| Guilds available: " + event.getGuildAvailableCount());
      info("|\033[1;95m       ██║██╔══██╗██╔══██╗  \033[0m| Owner ID: " + Contraption.ID);
      info("|\033[1;95m  ██   ██║██║  ██║██╔══██║  \033[0m| Guilds: " + guilds);
      info("|\033[1;95m  ╚█████╔╝██████╔╝██║  ██║  \033[0m| Shard ID: " + shardInfo.getShardId());
      info("|\033[1;95m   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  \033[0m| Invite URL: " + inviteURL);
      info("|                            | Account type: " + jda.getAccountType());
      info(String.format("|\033[1;92m    [version   %s]    \033[0m| WebSocket Ping: %s",
        JDAInfo.VERSION, jda.getGatewayPing()));
      info(String.format("|\033[1;92m    [dcl version %s]    \033[0m| API Ping: %s", Contraption.VERSION, api));
      info("|                            | Shards: " + shards);
    });
    PilotUtils.gc();
  }
}
