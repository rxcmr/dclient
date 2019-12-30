package com.fortuneteller.dclient.commands.music.children

import com.fortuneteller.dclient.commands.music.utils.TrackLoader
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.utils.EnvLoader
import com.fortuneteller.dclient.utils.PilotUtils
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.stream.Collectors

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
 */ /**
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
class SearchCommand : Command() {
  private var useCount = 0
  public override fun execute(event: CommandEvent) = event.let {
    if (it.args.isEmpty()) throw CommandException("Search term cannot be empty!")
    val client = it.jda.httpClient
    if (useCount <= 80) {
      val request = Request.Builder()
        .url(String.format(
          "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=%s&key=%s",
          it.args,
          EnvLoader.load("YT_API_KEY")
        )).build()
      try {
        client.newCall(request).execute().use { r ->
          useCount++
          if (!r.isSuccessful) {
            throw CommandException("Request failed.")
          } else {
            var json = ""
            BufferedReader(InputStreamReader(r.body()?.byteStream()!!)).use { i ->
              json = i.lines().map { l -> "$l\n" }
                .collect(Collectors.joining())
            }
            val itemsArray = JSONObject(json).getJSONArray("items")
            val videoID = itemsArray.getJSONObject(0).getJSONObject("id").getString("videoId")
            TrackLoader.instance.loadAndPlay(it.textChannel, "https://www.youtube.com/watch?v=$videoID")
          }
        }
      } catch (e: IOException) {
        throw CommandException(e.message)
      }
    } else {
      PilotUtils.warn("API limit reached, using scraper...")
      val request = Request.Builder().url("http://youtube-scrape.herokuapp.com/api/search?q=${it.args}&page=1")
        .build()
      try {
        client.newCall(request).execute().use { r ->
          if (!r.isSuccessful) {
            throw CommandException("Request failed.")
          }
          var json = ""
          BufferedReader(InputStreamReader(r.body()?.byteStream()!!)).use { i ->
            json = i.lines().map { l -> "$l\n" }
              .collect(Collectors.joining())
          }
          val itemsArray = JSONObject(json).getJSONArray("results")
          val videoURL = itemsArray.getJSONObject(1).getJSONObject("video").getString("url")
          TrackLoader.instance.loadAndPlay(it.textChannel, videoURL)
        }
      } catch (e: IOException) {
        throw CommandException(e.message)
      }
    }
  }

  init {
    name = "search"
    aliases = arrayOf("s")
    arguments = "**<query>**"
    help = "Search videos using YouTube API v3, or the scraper."
    category = Categories.MUSIC.category
    hidden = true
  }
}