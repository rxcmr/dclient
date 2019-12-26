package com.fortuneteller.dclient.commands.utils

import com.fortuneteller.dclient.utils.PilotUtils.Companion.info
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

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
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
interface SQLUtils {
  companion object {
    @Throws(SQLException::class)
    fun connect(): Connection {
      val url = "jdbc:sqlite:C:/Users/Marvin/IdeaProjects/dclient/src/main/resources/PilotDB.sqlite"
      return DriverManager.getConnection(url)
    }

    @Throws(SQLException::class)
    fun createDatabase() = info(connect().metaData.driverName)
  }

  @Throws(SQLException::class)
  fun createTable()

  @Throws(SQLException::class)
  fun insert(mode: SQLItemMode, vararg args: String?)

  @Throws(SQLException::class)
  fun select(mode: SQLItemMode, vararg args: String?)

  @Throws(SQLException::class)
  fun delete(mode: SQLItemMode, vararg args: String?)

  @Throws(SQLException::class)
  fun update(mode: SQLItemMode, vararg args: String?)

  @Throws(SQLException::class)
  fun exists(mode: SQLItemMode, vararg args: String?): Boolean
}