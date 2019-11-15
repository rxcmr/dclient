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
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class PurgeCommand extends Command {
  public PurgeCommand() {
    name = "purge";
    aliases = new String[]{"clear"};
    botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    arguments = "**<amount>** [1-100]";
    guildOnly = true;
    userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    cooldown = 5;
    help = "Purges [1-100] messages.";
    category = Categories.Utilities;
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    int amount = Integer.parseInt(event.getArgs());
    event.getChannel().sendTyping().queue();
    event.getChannel().getHistory().retrievePast(amount).queue(messages -> event.getChannel().purgeMessages(messages));
    event.getChannel().sendMessage("Cleared " + amount + " messages.").submit()
      .thenCompose(msg -> msg.delete().submitAfter(5, TimeUnit.SECONDS))
      .whenComplete((s, e) -> {
        if (e != null) event.reply("I was not able to remove my message.");
      });
  }
}
