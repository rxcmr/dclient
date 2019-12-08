package dcl.listeners;

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

import dcl.Skeleton;
import dcl.utils.GLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class ReadyEventListener extends ListenerAdapter {
  @Override
  public void onReady(@NotNull ReadyEvent event) {
    JDA jda = event.getJDA();
    JDA.ShardInfo shardInfo = jda.getShardInfo();
    String relativeShardString = String.format("[%s/%s]", shardInfo.getShardId() + 1, shardInfo.getShardTotal());
    String inviteURL = jda.getInviteUrl(
      Permission.BAN_MEMBERS,
      Permission.KICK_MEMBERS,
      Permission.MESSAGE_MANAGE,
      Permission.MANAGE_ROLES,
      Permission.MANAGE_SERVER
    );
    jda.getRestPing().queue(api -> {
      GLogger.info("|       R U N N I N G        | Status: " + jda.getStatus());
      GLogger.info("|                            | Logged in as: " + jda.getSelfUser().getAsTag());
      GLogger.info("|       ██╗██████╗  █████╗   | Guilds available: " + event.getGuildAvailableCount());
      GLogger.info("|       ██║██╔══██╗██╔══██╗  | Owner ID: " + Skeleton.ID);
      GLogger.info("|  ██   ██║██║  ██║██╔══██║  | Guilds: " + jda.getGuilds());
      GLogger.info("|  ╚█████╔╝██████╔╝██║  ██║  | Shard ID: " + shardInfo.getShardId());
      GLogger.info("|   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  | Invite URL: " + inviteURL);
      GLogger.info("|                            | Account type: " + jda.getAccountType());
      GLogger.info("|    [version   4.0.0_72]    | WebSocket Ping: " + jda.getGatewayPing());
      GLogger.info("|    [dcl version 1.6.3l]    | API Ping: " + api);
      GLogger.info("|                            | Shards: " + relativeShardString);
    });
  }
}
