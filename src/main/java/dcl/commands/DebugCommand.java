package dcl.commands;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import groovy.lang.GroovyShell;
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
public class DebugCommand extends Command {
   private final GroovyShell shell;
   private final String libs;

   public DebugCommand() {
      this.name = "debug";
      this.aliases = new String[]{"eval"};
      this.ownerCommand = true;
      this.help = "JDA evaluator using Groovy";
      this.hidden = true;
      this.category = new Category("Owner");
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
         "import net.dv8tion.jda.core.utils.*\n";
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

         event.reply(out == null ? "```Finished execution.```" : String.format("```%s```", out.toString()));
      } catch (Exception e) {
         event.reply("```java\n" + e + " \ncause: " + (e.getCause() == null ? "nothing" : e.getCause()) + "\n```");
      }
   }
}
