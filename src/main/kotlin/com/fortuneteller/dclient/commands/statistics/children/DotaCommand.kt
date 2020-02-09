package com.fortuneteller.dclient.commands.statistics.children

import com.fortuneteller.dclient.commands.statistics.StatisticsCommand
import com.fortuneteller.dclient.commands.statistics.utils.DotaStats
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.commands.utils.jsonResponse
import com.fortuneteller.dclient.utils.ExMessage
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import okhttp3.Request
import org.json.JSONObject
import java.time.Duration
import kotlin.math.abs

/*
 * Copyright 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *      Copyright (C) 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
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
@Suppress("unused")
class DotaCommand : StatisticsCommand() {
  override fun execute(event: CommandEvent) {
    val args = event.args.split("\\s+".toRegex())
    when (args[0]) {
      "matches" -> {
        url += "matches/${args[1]}"
        val request = Request.Builder().url(url).build()
        event.jda.httpClient.newCall(request).execute().use {
          if (!it.isSuccessful) throw CommandException(ExMessage.HTTP_FAILED)
          else {
            val json = it.jsonResponse
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
              .addField("Actual Player Count:", "${matchData.getInt("human_players")}", false)
              .addField("Match Duration:", matchTime, false)
              .addField("Game Mode:", DotaStats.getGameMode(matchData.getInt("game_mode")), false)
              .addField("Lobby Type:", DotaStats.getLobbyType(matchData.getInt("lobby_type")), false)
              .addField("Skill Bracket:", DotaStats.getSkill(matchData.getInt("skill")), false)
              .build()
            event.reply(embed)
          }
        }
      }
      "players" -> {
        url += "players/${args[1]}"
        val request = Request.Builder().url(url).build()
        event.jda.httpClient.newCall(request).execute().use {
          if (!it.isSuccessful) throw CommandException(ExMessage.HTTP_FAILED)
          else {
            val json = it.jsonResponse
            val playerData = JSONObject(json)
            val embed = EmbedBuilder()
              .addField("Account ID:", "${playerData.getInt("account_id")}", false)
              .addField("Persona Name:", playerData.getString("personaname"), false)
              .addField("Name:", playerData.getString("name"), false)
              .addField("Dota Plus:",
                if (playerData.getBoolean("plus")) "Subscribed" else "Not Subscribed", false)
              .addField("Steam ID:", playerData.getString("steamid"), false)
              .addField("Profile URL:", playerData.getString("playerurl"), false)
              .addField("Last login date:", playerData.getString("last_login"), false)
              .addField("Country Code:", playerData.getString("loccountrycode"), false)

            url += "wl"
            val wlRequest = Request.Builder().url(url).build()
            event.jda.httpClient.newCall(wlRequest).execute().use { wit ->
              if (!wit.isSuccessful) throw CommandException(ExMessage.HTTP_FAILED)
              else {
                val wlJson = wit.jsonResponse
                val wlStats = JSONObject(wlJson)
                val win = wlStats.getInt("win")
                val lose = wlStats.getInt("lose")

                embed
                  .addField("Win/Lose:", "$win/$lose", false)
                  .addField("Win rate:", "${(win / (win + lose)) * 100}%", false)
              }
            }

            event.reply(embed.build())
          }
        }
      }
      else -> throw CommandException()
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