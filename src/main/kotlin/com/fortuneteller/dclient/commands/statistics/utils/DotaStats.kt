package com.fortuneteller.dclient.commands.statistics.utils

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
object DotaStats {
  fun getLobbyType(id: Int) = when (id) {
    0 -> "Normal"
    1 -> "Practice"
    2 -> "Tournament"
    3 -> "Tutorial"
    4 -> "Co-op with Bots"
    5 -> "Ranked Team MM"
    6 -> "Ranked Solo MM"
    7 -> "Ranked"
    8 -> "1v1 Mid"
    9 -> "Battle Cup"
    else -> "UNKNOWN"
  }

  fun getSkill(id: Int) = when (id) {
    0 -> "Normal"
    1 -> "High"
    2 -> "Very High"
    else -> "UNKNOWN"
  }

  fun getGameMode(id: Int) = when (id) {
    0 -> "Unknown"
    1 -> "All Pick"
    2 -> "Captain's Mode"
    3 -> "Random Draft"
    4 -> "Single Draft"
    5 -> "All Random"
    6 -> "Intro"
    7 -> "Diretide"
    8 -> "Reverse Captain's Mode"
    9 -> "Greeviling"
    10 -> "Tutorial"
    11 -> "Mid Only"
    12 -> "Least Played"
    13 -> "Limited Heroes"
    14 -> "Compendium MM"
    15 -> "Custom"
    16 -> "Captain's Draft"
    17 -> "Balanced Draft"
    18 -> "Ability Draft"
    19 -> "Event"
    20 -> "All Random DM"
    21 -> "1v1 Mid"
    22 -> "All Draft"
    23 -> "Turbo"
    24 -> "Mutation"
    else -> "UNKNOWN"
  }
}