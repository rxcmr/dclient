package com.fortuneteller.dcl.commands.owner;
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


import com.fortuneteller.dcl.commands.utils.Categories;
import com.fortuneteller.dcl.commands.utils.CommandException;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class CustomQueryCommand extends Command {
  public CustomQueryCommand() {
    name = "sql";
    aliases = new String[]{"query"};
    ownerCommand = true;
    arguments = "**<SQLite query>**";
    help = "Executes a SQLite query.";
    category = Categories.OWNER.getCategory();
    hidden = true;
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String sql = event.getArgs();
    try (Connection connection = connect();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
      switch (resultSetMetaData.getColumnCount()) {
        case 1 -> {
          StringBuilder markdown = new StringBuilder(String.format("""
              ```ini
              [ %s ]
              """,
            resultSetMetaData.getColumnName(1)));
          while (resultSet.next()) markdown.append(String.format("""
              [ %s ]
              """,
            resultSet.getString(1)));
          markdown.append("\n```");
          event.reply(markdown.toString());
        }
        case 2 -> {
          StringBuilder markdown = new StringBuilder(String.format("""
              ```ini
              [ %s ] | [ %s ]
              """,
            resultSetMetaData.getColumnName(1),
            resultSetMetaData.getColumnName(2)));
          while (resultSet.next()) markdown.append(String.format("""
              [ %s ] | [ %s ]
              """,
            resultSet.getString(1),
            resultSet.getString(2)));
          markdown.append("\n```");
          event.reply(markdown.toString());
        }
        case 3 -> {
          StringBuilder markdown = new StringBuilder(String.format("""
              ```ini
              [ %s ] | [ %s ] | [ %s ]
              """,
            resultSetMetaData.getColumnName(1),
            resultSetMetaData.getColumnName(2),
            resultSetMetaData.getColumnName(3)));
          while (resultSet.next()) markdown.append(String.format("""
              [ %s ] | [ %s ] | [ %s ]
              """,
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getString(3)));
          markdown.append("\n```");
          event.reply(markdown.toString());
        }
        case 4 -> {
          StringBuilder markdown = new StringBuilder(String.format("""
              ```ini
              [ %s ] | [ %s ] | [ %s ] | [ %s ]
              """,
            resultSetMetaData.getColumnName(1),
            resultSetMetaData.getColumnName(2),
            resultSetMetaData.getColumnName(3),
            resultSetMetaData.getColumnName(4)));
          while (resultSet.next()) markdown.append(String.format("""
              [ %s ] | [ %s ] | [ %s ] | [ %s ]
              """,
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4)));
          markdown.append("\n```");
          event.reply(markdown.toString());
        }
        case 5 -> {
          StringBuilder markdown = new StringBuilder(String.format("""
              ```ini
              [ %s ] | [ %s ] | [ %s ] | [ %s ] | [ %s ]
              """,
            resultSetMetaData.getColumnName(1),
            resultSetMetaData.getColumnName(2),
            resultSetMetaData.getColumnName(3),
            resultSetMetaData.getColumnName(4),
            resultSetMetaData.getColumnName(5)));
          while (resultSet.next()) markdown.append(String.format("""
              [ %s ] | [ %s ] | [ %s ] | [ %s ] | [ %s ]
              """,
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4),
            resultSet.getString(5)));
          markdown.append("\n```");
          event.reply(markdown.toString());
        }
        default -> event.reply("Query finished without error.");
      }
    } catch (SQLException e) {
      throw new CommandException(e.getMessage(), e.getCause());
    }
  }

  public Connection connect() throws SQLException {
    String url = "jdbc:sqlite:C:/Users/Marvin/IdeaProjects/dclient/src/main/resources/JagTag.sqlite";
    return DriverManager.getConnection(url);
  }
}
