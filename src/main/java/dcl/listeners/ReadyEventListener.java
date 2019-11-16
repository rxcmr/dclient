package dcl.listeners;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com>.
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
 */

import ch.qos.logback.classic.Logger;
import dcl.Skeleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class ReadyEventListener extends ListenerAdapter {
  private Logger logger = (Logger) LoggerFactory.getLogger(ReadyEventListener.class);

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
      logger.info("|       R U N N I N G        | Status: " + jda.getStatus());
      logger.info("|                            | Logged in as: " + jda.getSelfUser().getAsTag());
      logger.info("|       ██╗██████╗  █████╗   | Guilds available: " + event.getGuildAvailableCount());
      logger.info("|       ██║██╔══██╗██╔══██╗  | Owner ID: " + Skeleton.ID);
      logger.info("|  ██   ██║██║  ██║██╔══██║  | Guilds: " + jda.getGuilds());
      logger.info("|  ╚█████╔╝██████╔╝██║  ██║  | Shard ID: " + shardInfo.getShardId());
      logger.info("|   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  | Invite URL: " + inviteURL);
      logger.info("|                            | Account type: " + jda.getAccountType());
      logger.info("|     [version 4.0.0_61]     | WebSocket Ping: " + jda.getGatewayPing());
      logger.info("|    [dcl version 1.5.2d]    | API Ping: " + api);
      logger.info("|                            | Shards: " + relativeShardString);
    });
  }
}
