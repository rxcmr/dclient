package com.fortuneteller.dclient.commands.utils;

import com.fortuneteller.dclient.Contraption;
import com.fortuneteller.dclient.commands.gadgets.JagTagCommand;
import com.fortuneteller.dclient.commands.gadgets.PingCommand;
import com.fortuneteller.dclient.commands.moderation.SlowmodeCommand;
import com.fortuneteller.dclient.commands.music.children.LeaveCommand;
import com.fortuneteller.dclient.commands.music.children.PlayCommand;
import com.fortuneteller.dclient.commands.music.children.SearchCommand;
import com.fortuneteller.dclient.commands.owner.CustomQueryCommand;
import com.fortuneteller.dclient.commands.owner.ShutdownCommand;
import com.fortuneteller.dclient.commands.owner.TestCommand;
import com.fortuneteller.dclient.utils.PilotUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
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
@SuppressWarnings("unused")
public class PilotCommandListener implements CommandListener {
  @Override
  public void onCommandException(@NotNull CommandEvent event, @NotNull Command command, @NotNull Throwable throwable) {
    final var owner = event.getJDA().getUserById(Contraption.id);
    event.getChannel().sendTyping().queue();
    if (command instanceof PingCommand) event.reply("Request did not go through.");
    else if (command instanceof TestCommand) event.reply(String.format("""
      ```java
      Test complete.
      Threw:
      %s
      ```""", throwable));
    else if (command instanceof JagTagCommand || command instanceof SlowmodeCommand)
      event.reply(throwable.getMessage());
    else if (command instanceof CustomQueryCommand) event.reply("Not valid SQLite query.");
    else if (command instanceof PlayCommand) new SearchCommand().execute(event);
    else {
      event.reply(
        command.getArguments() == null
          ? "Something wrong happened..."
          : Contraption.instance.getPrefix() + command.getName() + " " + command.getArguments()
      );
    }
    if (!(command instanceof PlayCommand)) {
      event.getMessage().addReaction("\uD83D\uDE41").queue();
      DirectMessage.sendStaticDirectMessage(
        "```java\n", Objects.requireNonNull(owner), String.format("%s%n```", throwable));
    }
  }

  @Override
  public void onCompletedCommand(@NotNull CommandEvent event, Command command) {
    try {
      if (command instanceof ShutdownCommand) return;
      event.getMessage().addReaction("\uD83D\uDE42").queue();
    } catch (Exception e) {
      PilotUtils.Companion.warn(e.getCause().toString());
    }
  }

  @Override
  public void onTerminatedCommand(@NotNull CommandEvent event, Command command) {
    if (command instanceof LeaveCommand) return;
    final var owner = Objects.requireNonNull(event.getJDA().getUserById(Contraption.id));
    event.getMessage().addReaction("\uD83E\uDD2C").queue();
    event.getChannel().sendTyping().queue();
    event.reply("Unexpected behavior. Try again.");
    DirectMessage.sendStaticDirectMessage(
      "Unexpected behavior. Triggered by: ",
      owner, String.format(
        "%s in %sin%s",
        event.getAuthor().getName(),
        event.getGuild().getName(),
        event.getChannel().getName())
    );
  }
}
