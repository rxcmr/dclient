package dcl.commands;

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


import com.jagrosh.jagtag.JagTag;
import com.jagrosh.jagtag.Method;
import com.jagrosh.jagtag.Parser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.commands.utils.CommandException;
import dcl.commands.utils.SQLItemMode;
import dcl.commands.utils.SQLUtils;
import dcl.utils.GLogger;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.StringJoiner;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
class Tag {
  public String tagKey;
  public String tagValue;
  public String ownerID;
  public String guildID;

  public String getTagKey() {
    return tagKey;
  }

  public void setTagKey(String tagKey) {
    this.tagKey = tagKey;
  }

  public String getTagValue() {
    return tagValue;
  }

  public void setTagValue(String tagValue) {
    this.tagValue = tagValue;
  }

  public String getOwnerID() {
    return ownerID;
  }

  public void setOwnerID(String ownerID) {
    this.ownerID = ownerID;
  }

  public String getGuildID() {
    return guildID;
  }

  public void setGuildID(String guildID) {
    this.guildID = guildID;
  }

  Tag set(String tagKey, String tagValue, String ownerID, String guildID) {
    this.tagKey = tagKey;
    this.tagValue = tagValue;
    this.ownerID = ownerID;
    this.guildID = guildID;
    return this;
  }
}

public class JagTagCommand extends Command implements SQLUtils {
  private final HashSet<Tag> tags = new HashSet<>();
  private final HashSet<Tag> tagCache = new HashSet<>();

  public JagTagCommand() throws SQLException, IOException {
    name = "jagtag";
    aliases = new String[]{"tag", "t"};
    category = Categories.UTILITIES.getCategory();
    arguments = "**<modifier>** **<name>** **<content>**";
    help = "JagTag like in Spectra";
    final File db = new File("C:\\Users\\Marvin\\IdeaProjects\\dclient\\src\\main\\resources\\JagTag.sqlite");
    if (!db.exists()) {
      if (db.createNewFile()) {
        createDatabase();
        createTable();
      }
    } else {
      select(SQLItemMode.ALL);
      tags.addAll(tagCache);
    }
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String[] args = event.getArgs().split("\\s+");
    String authorID = event.getAuthor().getId();
    String guildID = event.getGuild().getId();
    Parser jagtag = buildParser(event);
    event.getChannel().sendTyping().queue();
    try {
      switch (args[0]) {
        case "global", "g" -> {
          switch (args[1]) {
            case "create", "new", "add" -> {
              if (args[2].matches(
                "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)")
              ) throw new CommandException("Be unique, these are reserved command parameters.");
              select(SQLItemMode.ALL);
              StringJoiner stringJoiner = new StringJoiner(" ");
              Arrays.stream(args).skip(3).forEachOrdered(stringJoiner::add);
              String value = stringJoiner.toString();
              insert(SQLItemMode.GVALUE, args[2], value, authorID);
              select(SQLItemMode.ALL);
              tags.clear();
              tags.addAll(tagCache);
            }
            case "delete", "remove" -> {
              select(SQLItemMode.ALL);
              if (exists(SQLItemMode.GVALUE, args[2])) delete(SQLItemMode.LVALUE, args[2], authorID);
              else throw new CommandException("Deleting something that does not exist.");
              select(SQLItemMode.ALL);
              tags.clear();
              tags.addAll(tagCache);
            }
            case "edit", "modify" -> {
              select(SQLItemMode.ALL);
              StringJoiner stringJoiner = new StringJoiner(" ");
              Arrays.stream(args).skip(3).forEachOrdered(stringJoiner::add);
              String value = stringJoiner.toString();
              update(SQLItemMode.GVALUE, args[2], value, authorID);
              select(SQLItemMode.ALL);
              tags.clear();
              tags.addAll(tagCache);
            }
          }
        }
        case "create", "new", "add" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw new CommandException("Use the global parameter.");
          if (args[1].matches(
            "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)")
          ) throw new CommandException("Be unique, these are reserved command parameters.");
          select(SQLItemMode.ALL);
          StringJoiner stringJoiner = new StringJoiner(" ");
          Arrays.stream(args).skip(2).forEachOrdered(stringJoiner::add);
          String value = stringJoiner.toString();
          insert(SQLItemMode.LVALUE, args[1], value, authorID, guildID);
          select(SQLItemMode.ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "delete", "remove" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw new CommandException("Use the global parameter.");
          select(SQLItemMode.ALL);
          if (exists(SQLItemMode.LVALUE, args[1], guildID)) delete(SQLItemMode.LVALUE, args[1], authorID, guildID);
          else throw new CommandException("Deleting something that does not exist.");
          select(SQLItemMode.ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "edit", "modify" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw new CommandException("Use the global parameter.");
          select(SQLItemMode.ALL);
          StringJoiner stringJoiner = new StringJoiner(" ");
          Arrays.stream(args).skip(2).forEachOrdered(stringJoiner::add);
          String value = stringJoiner.toString();
          update(SQLItemMode.LVALUE, args[1], value, authorID, guildID);
          select(SQLItemMode.ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "raw" -> {
          for (Tag t : tags) {
            if (t.getTagKey().equals(args[0])) {
              if (t.getGuildID().equals(guildID)) {
                event.reply(t.getTagValue());
              } else if (t.getGuildID().equals("GLOBAL")) {
                event.reply(t.getTagValue());
              } else throw new CommandException("Tag not found.");
            }
          }
        }
        case "cblkraw" -> {
          for (Tag t : tags) {
            if (t.getTagKey().equals(args[0])) {
              if (t.getGuildID().equals(guildID)) {
                event.reply("```" + t.getTagValue() + "```");
              } else if (t.getGuildID().equals("GLOBAL")) {
                event.reply("```" + t.getTagValue() + "```");
              } else throw new CommandException("Tag not found.");
            }
          }
        }
        case "eval" -> {
          event.reply("Type `!!stop` to exit.");
          String id = event.getChannel().getId();
          event.getJDA().addEventListener(
            new ListenerAdapter() {
              @Override
              public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
                if (event.getAuthor().isBot() || event.getAuthor().isFake() || !event.getChannel().getId().equals(id))
                  return;
                event.getChannel().sendTyping().queue();
                String[] message = event.getMessage().getContentRaw().split("\\s+");
                StringJoiner stringJoiner = new StringJoiner(" ");
                Arrays.stream(message).forEachOrdered(stringJoiner::add);
                Parser parser = buildParser(event);
                if (message[0].equals("!!stop"))
                  event.getJDA().removeEventListener(this);
                else event.getChannel().sendMessage(parser.parse(stringJoiner.toString())).queue();
              }
            }
          );
        }
        default -> {
          for (Tag t : tags) {
            if (t.getTagKey().equals(args[0])) {
              if (t.getGuildID().equals(guildID)) {
                event.reply(jagtag.parse(t.getTagValue()));
              } else if (t.getGuildID().equals("GLOBAL")) {
                event.reply(jagtag.parse(t.getTagValue()));
              } else throw new CommandException("Tag not found.");
            }
          }
        }
      }
    } catch (SQLException s) {
      event.reply(s.getMessage());
      switch (s.getErrorCode()) {
        case 1 -> throw new CommandException("SQLite: column doesn't exist");
        case 19 -> throw new CommandException("Tag exists or missing parameters.");
        default -> throw new CommandException(s.getMessage() + s.getMessage());
      }
    }
  }

  private Parser buildParser(@NotNull Object event) {
    if (event instanceof CommandEvent) {
      return JagTag.newDefaultBuilder().addMethods(Arrays.asList(
        new Method("author", e -> ((CommandEvent) event).getAuthor().getName()),
        new Method("strlen", e ->
          String.valueOf(((CommandEvent) event).getArgs().split("\\s+").length - 1)),
        new Method("date", e -> new SimpleDateFormat("MM-dd-yyyy").format(new Date()))
        )
      ).build();
    } else {
      assert event instanceof GuildMessageReceivedEvent;
      return JagTag.newDefaultBuilder().addMethods(Arrays.asList(
        new Method("author", e -> ((GuildMessageReceivedEvent) event).getAuthor().getName()),
        new Method("strlen", e ->
          String.valueOf(((GuildMessageReceivedEvent) event).getMessage().getContentRaw().split("\\s+").length)),
        new Method("date", e -> new SimpleDateFormat("MM-dd-yyyy").format(new Date()))
        )
      ).build();
    }
  }

  @Override
  public synchronized Connection connect() throws SQLException {
    String url = "jdbc:sqlite:C:/Users/Marvin/IdeaProjects/dclient/src/main/resources/JagTag.sqlite";
    return DriverManager.getConnection(url);
  }

  @Override
  public synchronized void createDatabase() throws SQLException {
    try (Connection connection = connect()) {
      if (connection != null) {
        DatabaseMetaData metaData = connection.getMetaData();
        GLogger.info(metaData.getDriverName());
      }
    }
  }

  @Override
  public synchronized void createTable() throws SQLException {
    String sql = """
      CREATE TABLE IF NOT EXISTS tags (
        tagKey TEXT NOT NULL,
        tagValue TEXT NOT NULL,
        ownerID TEXT NOT NULL,
        guildID TEXT NOT NULL,
        UNIQUE (tagKey, guildID) ON CONFLICT ABORT,
        CHECK (length (tagKey) != 0 AND length (tagValue) != 0)
      );
      PRAGMA tags.auto_vacuum = FULL;
      """;

    try (Connection connection = connect()) {
      connection.createStatement().execute(sql);
    }
  }

  @Override
  public synchronized void insert(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 3) throw new SQLException("Missing parameters.");
    String sql = "INSERT INTO tags(tagKey, tagValue, ownerID, guildID) VALUES(?, ?, ?, ?)";
    try (Connection connection = connect()) {
      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        switch (mode) {
          case LVALUE -> {
            preparedStatement.setString(1, args[0]);
            preparedStatement.setString(2, args[1]);
            preparedStatement.setString(3, args[2]);
            preparedStatement.setString(4, args[3]);
            preparedStatement.executeUpdate();
          }
          case GVALUE -> {
            preparedStatement.setString(1, args[0]);
            preparedStatement.setString(2, args[1]);
            preparedStatement.setString(3, args[2]);
            preparedStatement.setString(4, "GLOBAL");
            preparedStatement.executeUpdate();
          }
        }
      }
    }
  }

  @Override
  public synchronized void select(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    switch (mode) {
      case ALL -> {
        String sql = "SELECT tagKey, tagValue, ownerID, guildID FROM tags WHERE guildID LIKE '%'";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
          tagCache.clear();
          while (resultSet.next()) {
            tagCache.add(new Tag().set(
              resultSet.getString("tagKey"),
              resultSet.getString("tagValue"),
              resultSet.getString("ownerID"),
              resultSet.getString("guildID")
              )
            );
          }
        }
      }
      case KEY -> {
        String sql = "SELECT tagKey FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next()) {
            for (Tag t : tags) {
              if (t.getOwnerID().equals(resultSet.getString("tagKey")))
                tagCache.add(t);
            }
          }
        }
      }
      case LVALUE, GVALUE -> {
        String sql = "SELECT tagValue FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next()) {
            for (Tag t : tags) {
              if (t.getOwnerID().equals(resultSet.getString("tagValue")))
                tagCache.add(t);
            }
          }
        }
      }
      case ID -> {
        String sql = "SELECT ownerID FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next()) {
            for (Tag t : tags) {
              if (t.getOwnerID().equals(resultSet.getString("ownerID")))
                tagCache.add(t);
            }
          }
        }
      }
      case GID -> {
        String sql = "SELECT guildID FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next()) {
            for (Tag t : tags) {
              if (t.getOwnerID().equals(resultSet.getString("guildID")))
                tagCache.add(t);
            }
          }
        }
      }
      case KNI -> {
        String sql = "SELECT tagKey, ownerID FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next()) {
            for (Tag t : tags) {
              if (t.getOwnerID().equals(resultSet.getString("ownerID")))
                if (t.getTagKey().equals(resultSet.getString("tagKey")))
                  tagCache.add(t);
            }
          }
        }
      }
      default -> throw new SQLException("Mode incorrect.");
    }
  }

  @Override
  public synchronized void delete(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 2) return;
    String sql = "DELETE FROM tags WHERE tagKey = ? AND ownerID = ? AND guildID = ?";
    try (Connection connection = connect();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      switch (mode) {
        case LVALUE -> {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, args[1]);
          preparedStatement.setString(3, args[2]);
          preparedStatement.executeUpdate();
        }
        case GVALUE -> {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, args[1]);
          preparedStatement.setString(3, "GLOBAL");
          preparedStatement.executeUpdate();
        }
      }
    }
  }

  @Override
  public synchronized void update(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 3) return;
    String sql = "UPDATE tags SET tagValue = ? WHERE tagKey = ? AND ownerID = ? AND guildID = ?";
    try (Connection connection = connect();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      switch (mode) {
        case LVALUE -> {
          preparedStatement.setString(1, args[1]);
          preparedStatement.setString(2, args[0]);
          preparedStatement.setString(3, args[2]);
          preparedStatement.setString(4, args[3]);
          preparedStatement.executeUpdate();
        }
        case GVALUE -> {
          preparedStatement.setString(1, args[1]);
          preparedStatement.setString(2, args[0]);
          preparedStatement.setString(3, args[2]);
          preparedStatement.setString(4, "GLOBAL");
          preparedStatement.executeUpdate();
        }
      }
    }
  }

  @Override
  public boolean exists(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    String sql = "SELECT EXISTS(SELECT tagKey, guildID FROM tags WHERE tagKey = ? AND guildID = ?)";
    switch (mode) {
      case LVALUE -> {
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, args[1]);
          try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.getInt(1) == 1;
          }
        }
      }
      case GVALUE -> {
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, "GLOBAL");
          try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.getInt(1) == 1;
          }
        }
      }
      default -> {
        return false;
      }
    }
  }
}
