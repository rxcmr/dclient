package com.fortuneteller.dclient.commands.statistics.children

import com.fortuneteller.dclient.commands.statistics.StatisticsCommand
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Duration
import java.util.stream.Collectors
import kotlin.math.abs

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

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class DotaCommand : StatisticsCommand() {
  override fun execute(event: CommandEvent) {
    val args = event.args.split("\\s+".toRegex())
    when (args[0]) {
      "matches" -> {
        url += "matches/${args[1]}"
        val request = Request.Builder().url(url).build()
        event.jda.httpClient.newCall(request).execute().use {
          if (!it.isSuccessful) throw CommandException("Request failed.")
          else {
            val json = BufferedReader(InputStreamReader(it.body()?.byteStream()!!)).use { i ->
              i.lines().map { l -> "$l\n" }.collect(Collectors.joining())
            }
            val matchData = JSONObject(json)
            val matchTime = Duration.ofSeconds(matchData.getInt("duration").toLong()).let { d ->
              val sec = d.seconds
              val absSec = abs(sec)
              val time = String.format("%d:%02d:%02d", absSec / 3600, (absSec % 3600) / 60, absSec % 60)
              if (sec < 0) "-$time" else time
            }
            val embed = EmbedBuilder()
              .setTitle("Match ID: ${matchData.getInt("match_id")}")
              .setDescription(when (matchData.getBoolean("radiant_win")) {
                true -> "**Radiant Victory**"
                false -> "**Dire Victory**"
              })
              .addField("Score:", "Radiant: ${matchData.getInt("radiant_score")}," +
                " Dire: ${matchData.getInt("dire_score")}", false)
              .addField("Match Duration:", matchTime, false)
              .build()
            event.reply(embed)
          }
        }
      }
    }
  }

  init {
    name = "dota2"
    url = "https://api.opendota.com/api/"
    help = "Defense of the Ancients 2 statistics using OpenDota API."
    arguments = "**<data>**"
    category = Categories.STATS.category
    hidden = true
  }
}