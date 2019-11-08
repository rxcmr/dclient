package dcl.listeners;

import ch.qos.logback.classic.Logger;
import dcl.Skeleton;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

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

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class ReadyEventListener extends ListenerAdapter {
   private Logger logger = (Logger) LoggerFactory.getLogger(ReadyEventListener.class);

   @Override
   public void onReady(@NotNull ReadyEvent event) {
      logger.info("|       R U N N I N G        | Status: " + event.getJDA().getStatus());
      logger.info("|                            | Logged in as: " + event.getJDA().getSelfUser().getAsTag());
      logger.info("|       ██╗██████╗  █████╗   | Guilds available: " + event.getGuildAvailableCount());
      logger.info("|       ██║██╔══██╗██╔══██╗  | REST HTTP Ping: " + event.getJDA().getRestPing().complete());
      logger.info("|  ██   ██║██║  ██║██╔══██║  | WebSocket Ping: " + event.getJDA().getGatewayPing());
      logger.info("|  ╚█████╔╝██████╔╝██║  ██║  | Sharding: " + event.getJDA().getShardInfo().getShardString());
      logger.info("|   ╚════╝ ╚═════╝ ╚═╝  ╚═╝  | Invite URL: " + event.getJDA().getInviteUrl());
      logger.info("|                            | Account type: " + event.getJDA().getAccountType());
      logger.info("|     [version 4.0.0_56]     | Guilds: " + event.getJDA().getGuilds());
      logger.info("|                            | Owner ID: " + Skeleton.ID);
   }
}
