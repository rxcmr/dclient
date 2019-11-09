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
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class ShutdownCommand extends Command {
   public ShutdownCommand() {
      this.name = "shutdown";
      this.aliases = new String[]{"shutdown"};
      this.help = "Shutdown JDA, and process running it.";
      this.ownerCommand = true;
      this.guildOnly = false;
      this.category = Categories.ownerOnly;
      this.hidden = true;
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      Logger logger = (Logger) LoggerFactory.getLogger(ShutdownCommand.class);
      logger.warn("[!!] JDA shutting down.");
      event.getJDA().shutdownNow();
   }
}
