package dcl.commands.utils;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import dcl.Skeleton;
import dcl.commands.ShutdownCommand;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
public class FleshListener implements CommandListener {
   private Logger logger = (Logger) LoggerFactory.getLogger(FleshListener.class);
   private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
      a instanceof String
         ? (c == null
         ? d -> d.sendMessage((String) a).queue(e -> logger.info(e.getContentRaw()))
         : d -> d.sendMessage(a + c).queue(e -> logger.info(e.getContentRaw())))
         : (c == null
         ? d -> d.sendMessage(a.toString()).queue(e -> logger.info(e.getContentRaw()))
         : d -> d.sendMessage(a + c).queue(e -> logger.info(e.getContentRaw())))
   );

   @Override
   public void onCommandException(@NotNull CommandEvent event, @NotNull Command command, @NotNull Throwable throwable) {
      User owner = event.getJDA().getUserById(Skeleton.ID);
      event.getChannel().sendTyping().queue();
      event.getMessage().addReaction("\u274C").queue();
      event.reply(
         command.getArguments() == null ?
            "Something wrong happened..." : Skeleton.prefix + command.getName() + " " + command.getArguments()
      );
      assert owner != null;
      dm.send("```java\n", owner, String.format("%s\n```", throwable));
   }

   @Override
   public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
      try {
         if (command instanceof ShutdownCommand) return;
         event.getMessage().addReaction("\u2705").queue();
      } catch (Exception e) { logger.warn(e.getCause().toString()); }
   }

   @Override
   public void onTerminatedCommand(@NotNull CommandEvent event, Command command) {
      User owner = Objects.requireNonNull(event.getJDA().getUserById(Skeleton.ID));
      event.getMessage().addReaction("\u274C").queue();
      event.getChannel().sendTyping().queue();
      event.reply("Unexpected behavior. Try again.");
      dm.send(
         "Unexpected behavior. Triggered by: ",
         owner,
         event.getAuthor() + " in " + event.getGuild()
      );
   }
}
