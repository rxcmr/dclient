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
import dcl.Skeleton;
import dcl.commands.utils.Categories;
import dcl.commands.utils.GoogleSearchHandler;
import dcl.commands.utils.GoogleSearchResult;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class GoogleSearchCommand extends Command {
  public GoogleSearchCommand() {
    name = "google";
    aliases = new String[]{"search"};
    category = Categories.utilities;
    cooldown = 10;
    arguments = "**query**";
    help = "The Google Search API";
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    final User owner = event.getJDA().getUserById(Skeleton.ID);
    final String apiKey = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load().get("API_KEY");
    final String engineID = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load().get("ENGINE_ID");
    final String[] queryArray = event.getArgs().split("\\s+");
    String query = String.join(" ", queryArray);
    GoogleSearchHandler.init(apiKey);
    List<GoogleSearchResult> results = GoogleSearchHandler.performSearch(engineID, query);
    event.reply(results.get(0).getSuggestedResult());
  }
}
