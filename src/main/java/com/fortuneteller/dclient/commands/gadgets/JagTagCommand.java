package com.fortuneteller.dclient.commands.gadgets;

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


import com.fortuneteller.dclient.commands.utils.Categories;
import com.fortuneteller.dclient.commands.utils.CommandException;
import com.fortuneteller.dclient.commands.utils.SQLItemMode;
import com.fortuneteller.dclient.commands.utils.SQLUtils;
import com.jagrosh.jagtag.JagTag;
import com.jagrosh.jagtag.Method;
import com.jagrosh.jagtag.Parser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.fortuneteller.dclient.commands.utils.SQLItemMode.*;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class Tag {
  private String tagKey;
  private String tagValue;
  private String ownerID;
  private String guildID;

  public String getTagKey() {
    return tagKey;
  }

  public String getTagValue() {
    return tagValue;
  }

  public String getOwnerID() {
    return ownerID;
  }

  public String getGuildID() {
    return guildID;
  }

  Tag set(String tagKey, String tagValue, String ownerID, String guildID) {
    this.tagKey = tagKey;
    this.tagValue = tagValue;
    this.ownerID = ownerID;
    this.guildID = guildID;
    return this;
  }
}

@SuppressWarnings("unused")
public class JagTagCommand extends Command implements SQLUtils {
  private final HashSet<Tag> tags = new HashSet<>();
  private final HashSet<Tag> tagCache = new HashSet<>();

  public JagTagCommand() throws SQLException, IOException {
    name = "jagtag";
    aliases = new String[]{"tag", "t"};
    category = Categories.GADGETS.getCategory();
    arguments = "**<modifier>** **<name>** **<content>**";
    help = "JagTag like in Spectra";
    var db = new File("C:\\Users\\Marvin\\IdeaProjects\\dclient\\src\\main\\resources\\PilotDB.sqlite");
    if (!db.exists()) {
      if (db.createNewFile()) {
        createDatabase();
        createTable();
      }
    } else {
      select(ALL);
      tags.addAll(tagCache);
    }
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    var args = event.getArgs().split("\\s+");
    var authorID = event.getAuthor().getId();
    var guildID = event.getGuild().getId();
    var jagtag = buildParser(event);
    event.getChannel().sendTyping().queue();
    try {
      switch (args[0]) {
        case "global", "g" -> {
          switch (args[1]) {
            case "create", "new", "add" -> {
              if (args[2].matches(
                "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)")
              ) throw new CommandException("Be unique, these are reserved command parameters.");
              select(ALL);
              var tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "));
              if (!event.getMessage().getAttachments().isEmpty()) {
                insert(LVALUE,
                  String.join(" ", event.getMessage().getAttachments().get(0).getProxyUrl(), tagValue),
                  authorID);
              } else insert(GVALUE, args[2], tagValue, authorID);
              select(ALL);
              tags.clear();
              tags.addAll(tagCache);
            }
            case "delete", "remove" -> {
              select(ALL);
              if (exists(GVALUE, args[2])) delete(LVALUE, args[2], authorID);
              else throw new CommandException("Deleting something that does not exist.");
              select(ALL);
              tags.clear();
              tags.addAll(tagCache);
            }
            case "edit", "modify" -> {
              select(ALL);
              var tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "));
              update(GVALUE, args[2], tagValue, authorID);
              select(ALL);
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
          select(ALL);
          var tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));
          if (!event.getMessage().getAttachments().isEmpty()) {
            insert(LVALUE,
              String.join(" ", event.getMessage().getAttachments().get(0).getProxyUrl(), tagValue),
              authorID,
              guildID);
          } else insert(LVALUE, args[1], tagValue, authorID, guildID);
          select(ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "delete", "remove" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw new CommandException("Use the global parameter.");
          select(ALL);
          if (exists(LVALUE, args[1], guildID)) delete(LVALUE, args[1], authorID, guildID);
          else throw new CommandException("Deleting something that does not exist.");
          select(ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "edit", "modify" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw new CommandException("Use the global parameter.");
          select(ALL);
          var tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));
          update(LVALUE, args[1], tagValue, authorID, guildID);
          select(ALL);
          tags.clear();
          tags.addAll(tagCache);
        }
        case "raw" -> tags.stream().filter(t -> t.getTagKey().equals(args[0])).forEachOrdered(t -> {
          if (t.getGuildID().equals(guildID) || t.getGuildID().equals("GLOBAL")) event.reply(t.getTagValue());
          else throw new CommandException("Tag not found.");
        });
        case "cblkraw" -> tags.stream().filter(t -> t.getTagKey().equals(args[0])).forEachOrdered(t -> {
          if (t.getGuildID().equals(guildID) || t.getGuildID().equals("GLOBAL"))
            event.reply("```" + t.getTagValue() + "```");
          else throw new CommandException("Tag not found.");
        });
        case "eval" -> {
          event.reply("Type `!!stop` to exit.");
          var id = event.getChannel().getId();
          event.getJDA().addEventListener(
            new ListenerAdapter() {
              @Override
              public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
                if (event.getAuthor().isBot() || event.getAuthor().isFake() || !event.getChannel().getId().equals(id))
                  return;
                event.getChannel().sendTyping().queue();
                var message = event.getMessage().getContentRaw().split("\\s+");
                var args = String.join(" ", message);
                var parser = buildParser(event);
                if (message[0].equalsIgnoreCase("!!stop"))
                  event.getJDA().removeEventListener(this);
                else event.getChannel().sendMessage(parser.parse(args)).queue();
              }
            });
        }
        default -> tags.stream().filter(t -> t.getTagKey().equals(args[0])).forEachOrdered(t -> {
          if (t.getGuildID().equals(guildID)) {
            event.reply(jagtag.parse(t.getTagValue()));
          } else if (t.getGuildID().equals("GLOBAL")) {
            event.reply(jagtag.parse(t.getTagValue()));
          } else throw new CommandException("Tag not found.");
        });
      }
    } catch (SQLException s) {
      event.reply(s.getMessage());
      if (s.getErrorCode() == 19) throw new CommandException("Tag exists or missing parameters.");
      else throw new CommandException(s.getMessage() + s.getMessage());
    }
  }

  private Parser buildParser(@NotNull Object event) {
    var cvt = new AtomicReference<CommandEvent>();
    var gvt = new AtomicReference<GuildMessageReceivedEvent>();

    if (event instanceof CommandEvent) cvt.set((CommandEvent) event);
    else {
      assert event instanceof GuildMessageReceivedEvent;
      gvt.set((GuildMessageReceivedEvent) event);
    }

    if (cvt.get() != null) {
      return JagTag.newDefaultBuilder().addMethods(Arrays.asList(
        new Method("author", e -> cvt.get().getAuthor().getName()),
        new Method("mAuthor", e -> cvt.get().getAuthor().getAsMention()),
        new Method("guild", e -> cvt.get().getGuild().getName()),
        new Method("guildID", e -> cvt.get().getGuild().getId()),
        new Method("memberCount", e -> String.valueOf(cvt.get().getGuild().getMemberCount())),
        new Method("boostCount", e -> String.valueOf(cvt.get().getGuild().getBoostCount())),
        new Method("owner", e -> Objects.requireNonNull(cvt.get().getGuild().getOwner()).getEffectiveName()),
        new Method("ownerID", e -> Objects.requireNonNull(cvt.get().getGuild().getOwner()).getId()),
        new Method("roles", e -> cvt.get().getGuild()
          .getRoles().stream().map(Role::getName).collect(Collectors.joining(", "))),
        new Method("randMember", e -> cvt.get().getGuild().getMembers()
          .get(new SecureRandom().nextInt(cvt.get().getGuild().getMembers().size())).getEffectiveName()),
        new Method("randChannel", e -> cvt.get().getGuild().getChannels()
          .get(new SecureRandom().nextInt(cvt.get().getGuild().getChannels().size())).getName()),
        new Method("strlen", e -> String.valueOf(cvt.get().getArgs().split("\\s+").length - 1)),
        new Method("date", e -> new SimpleDateFormat("MM-dd-yyyy").format(new Date())))
      ).build();
    } else {
      return JagTag.newDefaultBuilder().addMethods(Arrays.asList(
        new Method("author", e -> gvt.get().getAuthor().getName()),
        new Method("mAuthor", e -> gvt.get().getAuthor().getAsMention()),
        new Method("guild", e -> gvt.get().getGuild().getName()),
        new Method("guildID", e -> gvt.get().getGuild().getId()),
        new Method("memberCount", e -> String.valueOf(gvt.get().getGuild().getMemberCount())),
        new Method("boostCount", e -> String.valueOf(gvt.get().getGuild().getBoostCount())),
        new Method("owner", e -> Objects.requireNonNull(gvt.get().getGuild().getOwner()).getEffectiveName()),
        new Method("ownerID", e -> Objects.requireNonNull(gvt.get().getGuild().getOwner()).getId()),
        new Method("roles", e -> gvt.get().getGuild().getRoles().stream()
          .map(Role::getName).collect(Collectors.joining(", "))),
        new Method("randMember", e -> gvt.get().getGuild().getMembers()
          .get(new SecureRandom().nextInt(gvt.get().getGuild().getMembers().size())).getEffectiveName()),
        new Method("randChannel", e -> gvt.get().getGuild().getChannels()
          .get(new SecureRandom().nextInt(gvt.get().getGuild().getChannels().size())).getName()),
        new Method("strlen", e -> String.valueOf(gvt.get().getMessage()
          .getContentRaw().split("\\s+").length)),
        new Method("date", e -> new SimpleDateFormat("MM-dd-yyyy").format(new Date()))
        )
      ).build();
    }
  }

  @Override
  public void createTable() throws SQLException {
    var sql = """
      CREATE TABLE IF NOT EXISTS tags (
        tagKey TEXT NOT NULL,
        tagValue TEXT NOT NULL,
        ownerID TEXT NOT NULL,
        guildID TEXT NOT NULL,
        UNIQUE (tagKey, guildID) ON CONFLICT ABORT,
        CHECK (length (tagKey) != 0 AND length (tagValue) != 0)
      );
      PRAGMA auto_vacuum = FULL;
      """;

    try (var connection = connect()) {
      connection.createStatement().execute(sql);
    }
  }

  @Override
  public void insert(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 3) throw new SQLException("Missing parameters.");
    var sql = "INSERT INTO tags(tagKey, tagValue, ownerID, guildID) VALUES(?, ?, ?, ?)";
    try (var connection = connect()) {
      try (var preparedStatement = connection.prepareStatement(sql)) {
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
  public void select(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    switch (mode) {
      case ALL -> {
        var sql = "SELECT * FROM tags";
        try (var connection = connect();
             var preparedStatement = connection.prepareStatement(sql);
             var resultSet = preparedStatement.executeQuery()) {
          tagCache.clear();
          while (resultSet.next()) tagCache.add(new Tag().set(
            resultSet.getString("tagKey"),
            resultSet.getString("tagValue"),
            resultSet.getString("ownerID"),
            resultSet.getString("guildID")
            )
          );
        }
      }
      case LVALUE, GVALUE -> {
        String sql = "SELECT tagValue FROM tags";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
          tagCache.clear();
          while (resultSet.next())
            for (Tag t : tags) if (t.getOwnerID().equals(resultSet.getString("tagValue"))) tagCache.add(t);
        }
      }
      default -> throw new SQLException("Mode incorrect.");
    }
  }

  @Override
  public void delete(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 2) return;
    var sql = "DELETE FROM tags WHERE tagKey = ? AND ownerID = ? AND guildID = ?";
    try (var connection = connect();
         var preparedStatement = connection.prepareStatement(sql)) {
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
  public void update(@NotNull SQLItemMode mode, @NotNull String... args) throws SQLException {
    if (args.length < 3) return;
    var sql = "UPDATE tags SET tagValue = ? WHERE tagKey = ? AND ownerID = ? AND guildID = ?";
    try (var connection = connect();
         var preparedStatement = connection.prepareStatement(sql)) {
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
    var sql = "SELECT EXISTS(SELECT tagKey, guildID FROM tags WHERE tagKey = ? AND guildID = ?)";
    switch (mode) {
      case LVALUE -> {
        try (var connection = connect();
             var preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, args[1]);
          try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.getInt(1) == 1;
          }
        }
      }
      case GVALUE -> {
        try (var connection = connect();
             var preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, args[0]);
          preparedStatement.setString(2, "GLOBAL");
          try (var resultSet = preparedStatement.executeQuery()) {
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
