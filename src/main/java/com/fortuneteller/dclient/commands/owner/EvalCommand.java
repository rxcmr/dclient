package com.fortuneteller.dclient.commands.owner;

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

import com.fortuneteller.dclient.commands.utils.Categories;
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
public class EvalCommand extends Command {
  private final EmbedBuilder embedBuilder = new EmbedBuilder();
  private final GroovyShell shell;
  private final String imports;

  public EvalCommand() {
    name = "eval";
    aliases = new String[]{"dbg"};
    ownerCommand = true;
    help = "JDA evaluator using GroovyShell";
    arguments = "**<code>**";
    hidden = true;
    category = Categories.OWNER.getCategory();
    shell = new GroovyShell();
    imports = """
      import java.io.*;
      import java.lang.*;
      import java.util.*;
      import java.util.concurrent.*;
      import net.dv8tion.jda.api.*;
      import net.dv8tion.jda.api.entities.*;
      import net.dv8tion.jda.api.managers.*;
      import net.dv8tion.jda.api.utils.*;
      import com.fortuneteller.dclient.commands.gadgets.*;
      import com.fortuneteller.dclient.commands.owner.*;
      import com.fortuneteller.dclient.commands.music.*;
      import com.fortuneteller.dclient.commands.moderation.*;
      import com.fortuneteller.dclient.listeners.*;
      import com.fortuneteller.dclient.commands.utils.*;
      import com.fortuneteller.dclient.utils.*;
      """;
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String input = event.getArgs().replaceAll("(```[a-z]*)", "");
    try {
      shell.setProperty("args", event.getArgs());
      shell.setProperty("event", event);
      shell.setProperty("message", event.getMessage());
      shell.setProperty("channel", event.getChannel());
      shell.setProperty("jda", event.getJDA());
      shell.setProperty("guild", event.getGuild());
      shell.setProperty("member", event.getMember());
      shell.setProperty("user", event.getMember().getUser());
      event.reply(buildEmbed(shell.evaluate(imports + input), input));
      embedBuilder.clear();
    } catch (Exception e) {
      event.reply(exceptionEmbed(e, input));
      embedBuilder.clear();
    }
  }

  private @NotNull MessageEmbed buildEmbed(@Nullable Object output, @NotNull String args) {
    return output != null ? embedBuilder
      .setTitle("```Finished execution.```")
      .setDescription(String.format("**Command:** ```groovy%n%s%n```", args))
      .addField("**Output:** ", String.format("```%s```", output.toString()), false)
      .build() : embedBuilder
      .setTitle("```Finished execution.```")
      .setDescription(String.format("**Command:** ```groovy%n%s%n```", args))
      .build();
  }

  private @NotNull MessageEmbed exceptionEmbed(@NotNull Exception e, @NotNull String args) {
    var exceptionName = e.getClass().getCanonicalName().split("\\.");
    var javadoc = String.format(
      "https://docs.oracle.com/en/java/javase/13/docs/api/java.base/%s/%s/%s.html",
      exceptionName[0], exceptionName[1], exceptionName[2]
    );
    return embedBuilder
      .setTitle(String.format("```%s```", e.getClass().getSimpleName()),
        exceptionName[0].equalsIgnoreCase("java") ? javadoc : null
      )
      .setDescription(String.format("**Command:** ```groovy%n%s%n```", args))
      .addField("**Stack Trace:**", String.format("```%s```", e), false)
      .setColor(Color.RED)
      .build();
  }
}
