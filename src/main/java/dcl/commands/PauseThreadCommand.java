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
public class PauseThreadCommand extends Command {
   private Logger logger = (Logger) LoggerFactory.getLogger(PauseThreadCommand.class);

   public PauseThreadCommand() {
      name = "pausethread";
      aliases = new String[]{"halt"};
      ownerCommand = true;
      category = Categories.ownerOnly;
      arguments = "**amount** (seconds)";
      help = "Stops the current thread for a specific amount in time.";
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.reply("Stopping thread...");
      long start = System.currentTimeMillis();
      try {
         Thread.sleep(Integer.parseInt(event.getArgs()) * 1000);
      } catch (InterruptedException e) {
         logger.error("Thread paused.", e);
      }
      event.reply(String.format("Resumed after %d seconds.", (System.currentTimeMillis() - start) / 1000));
   }
}
