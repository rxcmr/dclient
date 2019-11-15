package dcl.commands;

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
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class DebugCommand extends Command {
  private final EmbedBuilder embedBuilder = new EmbedBuilder();
  private final GroovyShell shell;
  private final String libs;

  public DebugCommand() {
    name = "debug";
    aliases = new String[]{"eval"};
    ownerCommand = true;
    help = "JDA evaluator using GroovyShell";
    arguments = "**code**";
    hidden = true;
    category = Categories.Owner;
    shell = new GroovyShell();
    libs = "import java.io.*\n" +
      "import java.lang.*\n" +
      "import java.util.*\n" +
      "import java.util.concurrent.*\n" +
      "import net.dv8tion.jda.core.*\n" +
      "import net.dv8tion.jda.core.entities.*\n" +
      "import net.dv8tion.jda.core.entities.impl.*\n" +
      "import net.dv8tion.jda.core.managers.*\n" +
      "import net.dv8tion.jda.core.managers.impl.*\n" +
      "import net.dv8tion.jda.core.utils.*\n" +
      "import dcl.commands.*\n" +
      "import dcl.listeners.*\n" +
      "import dcl.commands.utils.*\n" +
      "import dcl.music.*;\n";
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    Logger logger = (Logger) LoggerFactory.getLogger(DebugCommand.class);
    try {
      shell.setProperty("args", event.getArgs());
      shell.setProperty("event", event);
      shell.setProperty("message", event.getMessage());
      shell.setProperty("channel", event.getChannel());
      shell.setProperty("jda", event.getJDA());
      shell.setProperty("guild", event.getGuild());
      shell.setProperty("member", event.getMember());
      shell.setProperty("user", event.getMember().getUser());
      shell.setProperty("logger", logger);

      String script = libs + event.getMessage().getContentRaw().split("\\s+", 2)[1];
      Object out = shell.evaluate(script);

      event.reply(buildEmbed(out, event.getArgs()));
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
      .addField("**Stack Trace:**", String.format(
        "```java%n%s%nCause: %s%n```", e, e.getCause() == null ? "nothing" : e.getCause()), false
      )
      .setColor(Color.RED)
      .build();
  }
}
