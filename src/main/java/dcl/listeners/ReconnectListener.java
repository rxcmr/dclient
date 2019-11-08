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
import dcl.commands.utils.DirectMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class ReconnectListener extends ListenerAdapter {
   @Override
   public void onReconnect(@Nonnull ReconnectedEvent event) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      Logger logger = (Logger) LoggerFactory.getLogger(ReconnectListener.class);
      DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
         c == null ? d -> d.sendMessage(a).queue() : d -> d.sendMessage(a + c).queue()
      );
      logger.info("Reconnected!");
      logger.info("REST HTTP Ping: " + event.getJDA().getRestPing().complete());
      logger.info("WebSocket Ping: " + event.getJDA().getGatewayPing());
      assert owner != null;
      dm.send("Reconnected!", owner, null);
   }

   private void sendDirectMessage(@NotNull User user) {
      user.openPrivateChannel().queue(channel -> channel.sendMessage("Reconnected!").queue());
   }
}
