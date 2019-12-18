package com.fortuneteller.dcl.commands.utils;

import com.fortuneteller.dcl.Contraption;
import com.fortuneteller.dcl.commands.gadgets.JagTagCommand;
import com.fortuneteller.dcl.commands.owner.CustomQueryCommand;
import com.fortuneteller.dcl.commands.owner.LatencyCommand;
import com.fortuneteller.dcl.commands.owner.ShutdownCommand;
import com.fortuneteller.dcl.commands.owner.TestCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.fortuneteller.dcl.utils.PilotUtils.warn;

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
public class PilotCommandListener implements CommandListener {
  @Override
  public void onCommandException(@NotNull CommandEvent event, @NotNull Command command, @NotNull Throwable throwable) {
    final User owner = event.getJDA().getUserById(Contraption.ID);
    event.getChannel().sendTyping().queue();
    event.getMessage().addReaction("\uD83D\uDE41").queue();
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
          : Contraption.getInstance().getPrefix() + command.getName() + " " + command.getArguments()
      );
    }
    assert owner != null;
    DirectMessage.sendStaticDirectMessage("```java\n", owner, String.format("%s%n```", throwable));
  }

  @Override
  public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
    try {
      if (command instanceof ShutdownCommand) return;
      event.getMessage().addReaction("\uD83D\uDE42").queue();
    } catch (Exception e) {
      warn(e.getCause().toString());
    }
  }

  @Override
  public void onTerminatedCommand(@NotNull CommandEvent event, Command command) {
    final User owner = Objects.requireNonNull(event.getJDA().getUserById(Contraption.ID));
    event.getMessage().addReaction("\uD83E\uDD2C").queue();
    event.getChannel().sendTyping().queue();
    event.reply("Unexpected behavior. Try again.");
    DirectMessage.sendStaticDirectMessage(
      "Unexpected behavior. Triggered by: ",
      owner,
      event.getAuthor() + " in " + event.getGuild()
    );
  }
}
