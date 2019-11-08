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
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class BanCommand extends Command {
   public BanCommand() {
      this.name = "ban";
      this.arguments = "**user** **amount** (in days)";
      this.help = "Bans a user";
      this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
      this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
      this.category = new Category("Moderation");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      String[] args = event.getArgs().split("\\s+");
      event.getChannel().sendTyping().queue();
      event.getMessage().getMentionedMembers().get(0).ban(Integer.parseInt(args[1])).queue();
   }
}
