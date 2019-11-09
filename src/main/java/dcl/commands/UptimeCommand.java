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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class UptimeCommand extends Command {
   public UptimeCommand() {
      this.name = "uptime";
      this.help = "Bot uptime.";
      this.ownerCommand = true;
      this.hidden = true;
      this.category = Categories.ownerOnly;
   }

   @Override
   protected void execute(CommandEvent event) {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      long uptime = runtimeMXBean.getUptime();
      long uptimeInSeconds = uptime / 1000;
      long numberOfHours = uptimeInSeconds / (60 * 60);
      long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
      long numberOfSeconds = uptimeInSeconds % 60;

      event.getChannel().sendMessageFormat(
         "`%s:%s:%s`", new Object[]{numberOfHours, numberOfMinutes, numberOfSeconds}
         ).queue();
   }
}
