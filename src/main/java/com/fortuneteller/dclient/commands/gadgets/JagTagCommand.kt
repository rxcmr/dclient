package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.gadgets.utils.Tag
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.commands.utils.SQLItemMode
import com.fortuneteller.dclient.commands.utils.SQLItemMode.*
import com.fortuneteller.dclient.commands.utils.SQLUtils
import com.fortuneteller.dclient.commands.utils.SQLUtils.Companion.connect
import com.fortuneteller.dclient.commands.utils.SQLUtils.Companion.createDatabase
import com.jagrosh.jagtag.JagTag
import com.jagrosh.jagtag.Method
import com.jagrosh.jagtag.Parser
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File
import java.security.SecureRandom
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors
import kotlin.collections.HashSet

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
@SuppressWarnings("unused")
class JagTagCommand : Command(), SQLUtils {
  private val tags: HashSet<Tag> = HashSet()
  private val tagCache: HashSet<Tag> = HashSet()

  override fun execute(event: CommandEvent?) {
    val args = event?.args?.split("\\s+".toRegex())?.toTypedArray()
    val authorID = event?.author?.id
    val guildID = event?.guild?.id
    val jagtag = buildParser(event!!)
    var matchFound = false
    event.channel.sendTyping().queue()
    try {
      when (args?.get(0)) {
        "global", "g" -> {
          when (args[1]) {
            "create", "new", "add" -> {
              if (args[2].matches(
                  "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex())
              ) throw CommandException("Be unique, these are reserved command parameters.")
              select(ALL)
              val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
              if (event.message.attachments.isNotEmpty()) {
                insert(LVALUE, "${event.message.attachments[0].proxyUrl} $tagValue", authorID)
              } else insert(GVALUE, args[2], tagValue, authorID)
              select(ALL)
              tags.clear()
              tags.addAll(tagCache)
            }
            "delete", "remove" -> {
              select(ALL)
              if (exists(GVALUE, args[2])) delete(LVALUE, args[2], authorID)
              else throw CommandException("Deleting something that does not exist.")
              select(ALL)
              tags.clear()
              tags.addAll(tagCache)
            }
            "edit", "modify" -> {
              select(ALL)
              val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
              update(GVALUE, args[2], tagValue, authorID)
              select(ALL)
              tags.clear()
              tags.addAll(tagCache)
            }
          }
        }
        "create", "new", "add" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
          if (args[1].matches(
              "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex())
          ) throw CommandException("Be unique, these are reserved command parameters.")
          select(ALL)
          val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
          if (event.message.attachments.isNotEmpty()) {
            insert(LVALUE, "${event.message.attachments[0].proxyUrl} $tagValue", authorID, guildID)
          } else insert(LVALUE, args[1], tagValue, authorID, guildID)
          select(ALL)
          tags.clear()
          tags.addAll(tagCache)
        }
        "delete", "remove" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
          select(ALL)
          if (exists(LVALUE, args[1], guildID)) delete(LVALUE, args[1], authorID, guildID)
          else throw CommandException("Deleting something that does not exist.")
          select(ALL)
          tags.clear()
          tags.addAll(tagCache)
        }
        "edit", "modify" -> {
          if (event.isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
          select(ALL)
          val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
          update(LVALUE, args[1], tagValue, authorID, guildID)
          select(ALL)
          tags.clear()
          tags.addAll(tagCache)
        }
        "raw" -> tags.stream().filter { t -> t.tagKey == args[0] }.forEachOrdered { t ->
          if (t.guildID == guildID || t.guildID == "GLOBAL") event.reply(t.tagValue)
          else throw CommandException("Tag not found.")
        }
        "cblkraw" -> tags.stream().filter { t -> t.tagKey == args[0] }.forEachOrdered { t ->
          if (t.guildID == guildID || t.guildID == "GLOBAL") event.reply("```" + t.tagValue + "```")
          else throw CommandException("Tag not found.")
        }
        "eval" -> {
          event.reply("Type `!!stop` to exit.")
          val id = event.channel.id
          event.jda.addEventListener(
            object : ListenerAdapter() {
              override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
                if (event.author.isBot || event.author.isFake || event.channel.id != id) return
                event.channel.sendTyping().queue()
                val message = event.message.contentRaw.split("\\s+").toTypedArray()
                val arguments = message.joinToString(" ")
                val parser = buildParser(event)
                if (message[0].equals("!!stop", ignoreCase = true))
                  event.jda.removeEventListener(this)
                else event.channel.sendMessage(parser.parse(arguments)).queue()
              }
            }
          )
        }
        else -> tags.stream().filter { t -> t.tagKey == args?.get(0) }
          .takeWhile { !matchFound }
          .forEachOrdered { t ->
            when (t.guildID) {
              guildID -> {
                matchFound = true
                event.reply(jagtag.parse(t.tagValue))
              }
              "GLOBAL" -> {
                matchFound = true
                event.reply("No local tags of the name ${t.tagKey}.")
                event.reply(jagtag.parse(t.tagValue))
              }
              else -> throw CommandException("Tag not found.")
            }
          }
      }
    } catch (s: SQLException) {
      event.reply(s.message)
      if (s.errorCode == 19) throw CommandException("Tag exists or missing parameters.")
      else throw CommandException(s.message + s.message)
    }

  }

  private fun buildParser(event: Any): Parser {
    val cvt = AtomicReference<CommandEvent>()
    val gvt = AtomicReference<GuildMessageReceivedEvent>()

    if (event is CommandEvent) cvt.set(event)
    else gvt.set(event as GuildMessageReceivedEvent)

    if (cvt.get() != null) {
      return JagTag.newDefaultBuilder().addMethods(listOf(
        Method("author") { _ -> cvt.get().author.name },
        Method("mAuthor") { _ -> cvt.get().author.asMention },
        Method("guild") { _ -> cvt.get().guild.name },
        Method("guildID") { _ -> cvt.get().guild.id },
        Method("memberCount") { _ -> cvt.get().guild.memberCount.toString() },
        Method("boostCount") { _ -> cvt.get().guild.boostCount.toString() },
        Method("owner") { _ -> cvt.get().guild.owner?.effectiveName },
        Method("ownerID") { _ -> cvt.get().guild.ownerId },
        Method("roles") { _ ->
          cvt.get().guild.roles.stream()
            .map(Role::getName).collect(Collectors.joining(", "))
        },
        Method("randMember") { _ ->
          cvt.get().guild.members[SecureRandom().nextInt(cvt.get().guild.members.size)]
            .effectiveName
        },
        Method("randChannel") { _ ->
          cvt.get().guild
            .channels[SecureRandom().nextInt(cvt.get().guild.channels.size)].name
        },
        Method("strlen") { _ -> (cvt.get().args.split("\\s+".toRegex()).size - 1).toString() },
        Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) }
      )).build()
    } else {
      return JagTag.newDefaultBuilder().addMethods(listOf(
        Method("author") { _ -> gvt.get().author.name },
        Method("mAuthor") { _ -> gvt.get().author.asMention },
        Method("guild") { _ -> gvt.get().guild.name },
        Method("guildID") { _ -> gvt.get().guild.id },
        Method("memberCount") { _ -> gvt.get().guild.memberCount.toString() },
        Method("boostCount") { _ -> gvt.get().guild.boostCount.toString() },
        Method("owner") { _ -> gvt.get().guild.owner?.effectiveName },
        Method("ownerID") { _ -> gvt.get().guild.ownerId },
        Method("roles") { _ ->
          gvt.get().guild.roles.stream()
            .map(Role::getName).collect(Collectors.joining(", "))
        },
        Method("randMember") { _ ->
          gvt.get().guild.members[SecureRandom().nextInt(cvt.get().guild.members.size)]
            .effectiveName
        },
        Method("randChannel") { _ ->
          gvt.get().guild
            .channels[SecureRandom().nextInt(cvt.get().guild.channels.size)].name
        },
        Method("strlen") { _ -> (gvt.get().message.contentRaw.split("\\s+".toRegex()).size - 1).toString() },
        Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) }
      )).build()
    }
  }

  override fun createTable() {
    val sql = """
      CREATE TABLE IF NOT EXISTS tags (
        tagKey TEXT NOT NULL,
        tagValue TEXT NOT NULL,
        ownerID TEXT NOT NULL,
        guildID TEXT NOT NULL,
        UNIQUE (tagKey, guildID) ON CONFLICT ABORT,
        CHECK (length (tagKey) != 0 AND length (tagValue) != 0)
      );
      PRAGMA auto_vacuum = FULL;
      """

    connect().createStatement().execute(sql)
  }

  override fun insert(mode: SQLItemMode, vararg args: String?) {
    val sql = "INSERT INTO tags(tagKey, tagValue, ownerID, guildID) VALUES(?, ?, ?, ?)"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    when (mode) {
      LVALUE -> {
        with(preparedStatement) {
          setString(1, args[0])
          setString(2, args[1])
          setString(3, args[2])
          setString(4, args[3])
          executeUpdate()
        }
      }
      GVALUE -> {
        with(preparedStatement) {
          setString(1, args[0])
          setString(2, args[1])
          setString(3, args[2])
          setString(4, "GLOBAL")
          executeUpdate()
        }
      }
      else -> return
    }
  }

  override fun select(mode: SQLItemMode, vararg args: String?) {
    when (mode) {
      ALL -> {
        val sql = "SELECT * FROM tags"
        val connection = connect()
        val preparedStatement = connection.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery()
        tagCache.clear()
        while (resultSet.next()) tagCache.add(Tag().set(
          resultSet.getString("tagKey"),
          resultSet.getString("tagValue"),
          resultSet.getString("ownerID"),
          resultSet.getString("guildID")
        ))
      }
      LVALUE, GVALUE -> {
        val sql = "SELECT tagValue FROM tags"
        val connection = connect()
        val preparedStatement = connection.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery()
        tagCache.clear()
        while (resultSet.next())
          for (t in tags) if (t.ownerID == resultSet.getString("tagValue")) tagCache.add(t)
      }
      else -> return
    }
  }

  override fun delete(mode: SQLItemMode, vararg args: String?) {
    val sql = "DELETE FROM tags WHERE tagKey = ? AND ownerID = ? AND guildID = ?"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    when (mode) {
      LVALUE -> {
        preparedStatement.setString(1, args[0])
        preparedStatement.setString(2, args[1])
        preparedStatement.setString(3, args[2])
        preparedStatement.executeUpdate()
      }
      GVALUE -> {
        preparedStatement.setString(1, args[0])
        preparedStatement.setString(2, args[1])
        preparedStatement.setString(3, "GLOBAL")
        preparedStatement.executeUpdate()
      }
      else -> return
    }
  }

  override fun update(mode: SQLItemMode, vararg args: String?) {
    val sql = "UPDATE tags SET tagValue = ? WHERE tagKey = ? AND ownerID = ? AND guildID = ?"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    when (mode) {
      LVALUE -> {
        preparedStatement.setString(1, args[1])
        preparedStatement.setString(2, args[0])
        preparedStatement.setString(3, args[2])
        preparedStatement.setString(4, args[3])
        preparedStatement.executeUpdate()
      }
      GVALUE -> {
        preparedStatement.setString(1, args[1])
        preparedStatement.setString(2, args[0])
        preparedStatement.setString(3, args[2])
        preparedStatement.setString(4, "GLOBAL")
        preparedStatement.executeUpdate()
      }
      else -> return
    }
  }

  override fun exists(mode: SQLItemMode, vararg args: String?): Boolean {
    val sql = "SELECT EXISTS(SELECT tagKey, guildID FROM tags WHERE tagKey = ? AND guildID = ?)"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    return when (mode) {
      LVALUE -> {
        preparedStatement.setString(1, args[0])
        preparedStatement.setString(2, args[1])
        val resultSet = preparedStatement.executeQuery()
        resultSet.getInt(1) == 1
      }
      GVALUE -> {
        preparedStatement.setString(1, args[0])
        preparedStatement.setString(2, "GLOBAL")
        val resultSet = preparedStatement.executeQuery()
        resultSet.getInt(1) == 1
      }
      else -> false
    }
  }

  init {
    name = "jagtag"
    aliases = arrayOf("tag", "t")
    category = Categories.GADGETS.category
    arguments = "**<modifier>** **<name>** **<content>**"
    help = "JagTag like in Spectra"
    val db = File("C:\\Users\\Marvin\\IdeaProjects\\dclient\\src\\main\\resources\\PilotDB.sqlite")
    if (!db.exists()) {
      if (db.createNewFile()) {
        createDatabase()
        createTable()
      }
    } else {
      select(ALL)
      tags.addAll(tagCache)
    }
  }
}