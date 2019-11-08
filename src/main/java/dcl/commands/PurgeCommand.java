package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
public class PurgeCommand extends Command {
   public PurgeCommand() {
      this.name = "purge";
      this.aliases = new String[]{"clear"};
      this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
      this.arguments = "**amount** [1-100]";
      this.guildOnly = true;
      this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
      this.cooldown = 5;
      this.help = "Purges [1-100] messages.";
      this.category = new Category("Moderation");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      int amount = Integer.parseInt(event.getArgs());
      event.getChannel().sendTyping().queue();
      List<Message> messages = event.getChannel().getHistory().retrievePast(amount).complete();
      event.getChannel().purgeMessages(messages);
      event.reply("Cleared " + amount + " messages.");
   }
}
