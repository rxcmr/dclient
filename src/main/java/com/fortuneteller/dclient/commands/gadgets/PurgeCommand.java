package com.fortuneteller.dclient.commands.gadgets;

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
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
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
    category = Categories.GADGETS.getCategory();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
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