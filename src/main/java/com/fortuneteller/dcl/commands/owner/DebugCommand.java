package com.fortuneteller.dcl.commands.owner;

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

import com.fortuneteller.dcl.commands.utils.Categories;
import com.fortuneteller.dcl.utils.PilotUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class DebugCommand extends Command {
  private final EmbedBuilder embedBuilder = new EmbedBuilder();
  private final GroovyShell shell;
  private final String imports;

  public DebugCommand() {
    name = "debug";
    aliases = new String[]{"eval"};
    ownerCommand = true;
    help = "JDA evaluator using GroovyShell";
    arguments = "**<code>**";
    hidden = true;
    category = Categories.OWNER.getCategory();
    shell = new GroovyShell();
    imports = """
      import java.io.*
      import java.lang.*
      import java.util.*
      import java.util.concurrent.*
      import net.dv8tion.jda.api.*
      import net.dv8tion.jda.core.*
      import net.dv8tion.jda.core.entities.*
      import net.dv8tion.jda.core.entities.impl.*
      import net.dv8tion.jda.core.managers.*
      import net.dv8tion.jda.core.managers.impl.*
      import net.dv8tion.jda.core.utils.*
      import dcl.commands.*
      import dcl.listeners.*
      import dcl.commands.utils.*
      import dcl.music.*
      """;
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    try {
      shell.setProperty("args", event.getArgs());
      shell.setProperty("event", event);
      shell.setProperty("message", event.getMessage());
      shell.setProperty("channel", event.getChannel());
      shell.setProperty("jda", event.getJDA());
      shell.setProperty("guild", event.getGuild());
      shell.setProperty("member", event.getMember());
      shell.setProperty("user", event.getMember().getUser());
      shell.setProperty("logger", PilotUtils.class);

      String script = imports + event.getMessage().getContentRaw().split("\\s+", 2)[1];
      Object output = shell.evaluate(script);

      event.reply(buildEmbed(output, event.getArgs()));
      embedBuilder.clear();
    } catch (Exception e) {
      event.reply(exceptionEmbed(e, event.getArgs()));
      embedBuilder.clear();
    }
  }

  @NotNull
  private MessageEmbed buildEmbed(@Nullable Object output, @NotNull String args) {
    if (output != null) {
      return embedBuilder
        .setTitle("```Finished execution.```")
        .setDescription(String.format("**Command:** ```%s```", args))
        .addField("**Output:** ", String.format("```java%n%s%n```", output), false)
        .build();
    } else {
      return embedBuilder
        .setTitle("```Finished execution.```")
        .setDescription(String.format("**Command:** ```%s```", args))
        .build();
    }
  }

  @NotNull
  private MessageEmbed exceptionEmbed(@NotNull Exception e, @NotNull String args) {
    String[] exceptionName = e.getClass().getCanonicalName().split("\\.");
    String javadoc = String.format(
      "https://docs.oracle.com/en/java/javase/13/docs/api/java.base/%s/%s/%s.html",
      exceptionName[0], exceptionName[1], exceptionName[2]
    );
    return embedBuilder
      .setTitle(String.format("```%s```", e.getClass().getSimpleName()),
        exceptionName[0].equalsIgnoreCase("java") ? javadoc : null
      )
      .setDescription(String.format("**Command:** ```%s```", args))
      .addField("**Stack Trace:**", String.format("```java%n%s```", e), false)
      .setColor(Color.RED)
      .build();
  }
}
