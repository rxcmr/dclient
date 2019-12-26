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

import com.fortuneteller.dclient.commands.gadgets.utils.GoogleSearchHandler;
import com.fortuneteller.dclient.commands.utils.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class GoogleSearchCommand extends Command {
  public GoogleSearchCommand() {
    name = "google";
    aliases = new String[]{"search"};
    category = Categories.GADGETS.getCategory();
    cooldown = 10;
    arguments = "**<query>**";
    help = "The Google Search API";
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue();
    final var apiKey = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load().get("API_KEY");
    final var engineID = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load().get("ENGINE_ID");
    final var queryArray = event.getArgs().split("\\s+");
    var query = String.join(" ", queryArray);
    GoogleSearchHandler.init(apiKey);
    var results = GoogleSearchHandler.performSearch(
      engineID, query, event.getJDA().getHttpClient());
    event.reply(results.get(0).getSuggestedResult());
  }
}
