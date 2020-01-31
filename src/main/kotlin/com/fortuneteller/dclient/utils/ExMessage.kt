package com.fortuneteller.dclient.utils

import net.dv8tion.jda.api.entities.TextChannel

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
enum class ExMessage(val msg: String) {
  JT_KEY_EMPTY("Name is empty!"),
  JT_VAL_EMPTY("Content is empty!"),
  JT_NOT_FOUND("Tag not found."),
  JT_RESERVED("Cannot use reserved command parameters."),
  JT_DELETE_NOTHING("Deleting something that doesn't exist."),
  JT_GLOBAL("Use the global parameter."),
  JT_EXISTS_OR_MISSING("Tag exists or missing parameters."),
  M_PAUSED("Playback is currently paused."),
  M_EMPTY_URL("URL cannot be empty!"),
  M_NOT_PAUSED("Playback is not paused."),
  M_EMPTY_SEARCH("Search term cannot be empty!"),
  M_ONGOING("Currently connected to a voice channel. (pl.m leave?)"),
  M_NOT_JOINED("Connect to a voice channel."),
  HTTP_FAILED("Request failed."),
  SLOWMODE_DURATION("Slow mode must not be negative or greater than ${TextChannel.MAX_SLOWMODE}."),
  INVALID_CLASS("Invalid class name."),
  INVALID_INTEGER("Not a valid integer."),
  INPUT_TOO_LONG("Input too long."),
  INVALID_DB("Invalid database name.")
}