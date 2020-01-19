package com.fortuneteller.dclient.utils

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
@Suppress("unused")
object Colors {
  private const val ZWS = "\u001b"
  const val RESET = "$ZWS[0m" // Text Reset

  // Regular Colors
  const val BLACK = "$ZWS[0;30m" // BLACK
  const val RED = "$ZWS[0;31m" // RED
  const val GREEN = "$ZWS[0;32m" // GREEN
  const val YELLOW = "$ZWS[0;33m" // YELLOW
  const val BLUE = "$ZWS[0;34m" // BLUE
  const val PURPLE = "$ZWS[0;35m" // PURPLE
  const val CYAN = "$ZWS[0;36m" // CYAN
  const val WHITE = "$ZWS[0;37m" // WHITE

  // Bold
  const val BLACK_BOLD = "$ZWS[1;30m" // BLACK
  const val RED_BOLD = "$ZWS[1;31m" // RED
  const val GREEN_BOLD = "$ZWS[1;32m" // GREEN
  const val YELLOW_BOLD = "$ZWS[1;33m" // YELLOW
  const val BLUE_BOLD = "$ZWS[1;34m" // BLUE
  const val PURPLE_BOLD = "$ZWS[1;35m" // PURPLE
  const val CYAN_BOLD = "$ZWS[1;36m" // CYAN
  const val WHITE_BOLD = "$ZWS[1;37m" // WHITE

  // Underline
  const val BLACK_UNDERLINED = "$ZWS[4;30m" // BLACK
  const val RED_UNDERLINED = "$ZWS[4;31m" // RED
  const val GREEN_UNDERLINED = "$ZWS[4;32m" // GREEN
  const val YELLOW_UNDERLINED = "$ZWS[4;33m" // YELLOW
  const val BLUE_UNDERLINED = "$ZWS[4;34m" // BLUE
  const val PURPLE_UNDERLINED = "$ZWS[4;35m" // PURPLE
  const val CYAN_UNDERLINED = "$ZWS[4;36m" // CYAN
  const val WHITE_UNDERLINED = "$ZWS[4;37m" // WHITE

  // Background
  const val BLACK_BACKGROUND = "$ZWS[40m" // BLACK
  const val RED_BACKGROUND = "$ZWS[41m" // RED
  const val GREEN_BACKGROUND = "$ZWS[42m" // GREEN
  const val YELLOW_BACKGROUND = "$ZWS[43m" // YELLOW
  const val BLUE_BACKGROUND = "$ZWS[44m" // BLUE
  const val PURPLE_BACKGROUND = "$ZWS[45m" // PURPLE
  const val CYAN_BACKGROUND = "$ZWS[46m" // CYAN
  const val WHITE_BACKGROUND = "$ZWS[47m" // WHITE

  // High Intensity
  const val BLACK_BRIGHT = "$ZWS[0;90m" // BLACK
  const val RED_BRIGHT = "$ZWS[0;91m" // RED
  const val GREEN_BRIGHT = "$ZWS[0;92m" // GREEN
  const val YELLOW_BRIGHT = "$ZWS[0;93m" // YELLOW
  const val BLUE_BRIGHT = "$ZWS[0;94m" // BLUE
  const val PURPLE_BRIGHT = "$ZWS[0;95m" // PURPLE
  const val CYAN_BRIGHT = "$ZWS[0;96m" // CYAN
  const val WHITE_BRIGHT = "$ZWS[0;97m" // WHITE

  // Bold High Intensity
  const val BLACK_BOLD_BRIGHT = "$ZWS[1;90m" // BLACK
  const val RED_BOLD_BRIGHT = "$ZWS[1;91m" // RED
  const val GREEN_BOLD_BRIGHT = "$ZWS[1;92m" // GREEN
  const val YELLOW_BOLD_BRIGHT = "$ZWS[1;93m" // YELLOW
  const val BLUE_BOLD_BRIGHT = "$ZWS[1;94m" // BLUE
  const val PURPLE_BOLD_BRIGHT = "$ZWS[1;95m" // PURPLE
  const val CYAN_BOLD_BRIGHT = "$ZWS[1;96m" // CYAN
  const val WHITE_BOLD_BRIGHT = "$ZWS[1;97m" // WHITE

  // High Intensity backgrounds
  const val BLACK_BACKGROUND_BRIGHT = "$ZWS[0;100m" // BLACK
  const val RED_BACKGROUND_BRIGHT = "$ZWS[0;101m" // RED
  const val GREEN_BACKGROUND_BRIGHT = "$ZWS[0;102m" // GREEN
  const val YELLOW_BACKGROUND_BRIGHT = "$ZWS[0;103m" // YELLOW
  const val BLUE_BACKGROUND_BRIGHT = "$ZWS[0;104m" // BLUE
  const val PURPLE_BACKGROUND_BRIGHT = "$ZWS[0;105m" // PURPLE
  const val CYAN_BACKGROUND_BRIGHT = "$ZWS[0;106m" // CYAN
  const val WHITE_BACKGROUND_BRIGHT = "$ZWS[0;107m" // WHITE
}