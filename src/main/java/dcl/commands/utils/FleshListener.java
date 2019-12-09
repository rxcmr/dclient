package dcl.commands.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import dcl.Skeleton;
import dcl.commands.*;
import dcl.utils.GLogger;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
public class FleshListener implements CommandListener {
  private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
    a instanceof String
      ? (c == null
      ? d -> d.sendMessage((String) a).queue(e -> GLogger.info(e.getContentRaw().replace("```", "")))
      : d -> d.sendMessage(a + c).queue(e -> GLogger.info(e.getContentRaw().replace("```", ""))))
      : (c == null
      ? d -> d.sendMessage(a.toString()).queue(e -> GLogger.info(e.getContentRaw().replace("```", "")))
      : d -> d.sendMessage(a + c).queue(e -> GLogger.info(e.getContentRaw().replace("```", ""))))
  );

  @Override
  public void onCommandException(@NotNull CommandEvent event, @NotNull Command command, @NotNull Throwable throwable) {
    User owner = event.getJDA().getUserById(Skeleton.ID);
    event.getChannel().sendTyping().queue();
    event.getMessage().addReaction("\u274C").queue();
    if (command instanceof LatencyCommand) event.reply("Request did not go through.");
    else if (command instanceof TestCommand) event.reply("""
      ```java
      Test complete.
      Threw:
      """ + throwable + """
      ```
      """);
    else if (command instanceof JagTagCommand) event.reply(throwable.getMessage());
    else if (command instanceof CustomQueryCommand) event.reply("Not valid SQLite query.");
    else {
      event.reply(
        command.getArguments() == null
          ? "Something wrong happened..."
          : Skeleton.prefix + command.getName() + " " + command.getArguments()
      );
    }
    assert owner != null;
    dm.send("```java\n", owner, String.format("%s%n```", throwable));
  }

  @Override
  public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
    try {
      if (command instanceof ShutdownCommand) return;
      event.getMessage().addReaction("\u2705").queue();
    } catch (Exception e) {
      GLogger.warn(e.getCause().toString());
    }
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
