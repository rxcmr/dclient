package com.fortuneteller.dclient.commands.music.children;

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


import com.fortuneteller.dclient.commands.music.utils.TrackLoader;
import com.fortuneteller.dclient.commands.utils.Categories;
import com.fortuneteller.dclient.commands.utils.CommandException;
import com.fortuneteller.dclient.utils.PilotUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Request;
import okhttp3.Request.Builder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class SearchCommand extends Command {
  private int useCount = 0;

  public SearchCommand() {
    name = "search";
    aliases = new String[]{"s"};
    arguments = "**<query>**";
    help = "Search videos using YouTube API v3.";
    category = Categories.MUSIC.getCategory();
    hidden = true;
  }

  @Override
  public void execute(@NotNull CommandEvent event) {
    if (event.getArgs().isEmpty()) throw new CommandException("Search term cannot be empty!");
    var client = event.getJDA().getHttpClient();
    if (useCount <= 80) {
      var apiKey = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load().get("YT_API_KEY");
      var request = new Request.Builder()
        .url(String.format(
          "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=%s&key=%s",
          event.getArgs(),
          apiKey
        )).build();
      try (var response = client.newCall(request).execute()) {
        useCount++;
        if (!response.isSuccessful()) {
          throw new CommandException("Request failed.");
        } else {
          var json = "";
          try (
            var in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(response.body()).byteStream()))
          ) {
            json = in.lines().map(line -> line + "\n").collect(Collectors.joining());
          }
          var itemsArray = new JSONObject(json).getJSONArray("items");
          var videoID = itemsArray.getJSONObject(0).getJSONObject("id").getString("videoId");
          TrackLoader.getInstance().loadAndPlay(event.getTextChannel(),
            "https://www.youtube.com/watch?v=" + videoID);
        }
      } catch (IOException e) {
        throw new CommandException(e.getMessage());
      }
    } else {
      PilotUtils.warn("YouTube Search API limit reached, using scraper...");
      var request = new Builder().url(String.format(
        "http://youtube-scrape.herokuapp.com/api/search?q=%s&page=1",
        event.getArgs()
      )).build();
      try (var response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          throw new CommandException("Request failed.");
        }
        var json = "";
        try (
          var in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(response.body()).byteStream()))
        ) {
          json = in.lines().map(line -> line + "\n").collect(Collectors.joining());
        }
        var itemsArray = new JSONObject(json).getJSONArray("results");
        var videoURL = itemsArray.getJSONObject(1).getJSONObject("video").getString("url");
        TrackLoader.getInstance().loadAndPlay(event.getTextChannel(), videoURL);
      } catch (IOException e) {
        throw new CommandException(e.getMessage());
      }
    }
  }
}
