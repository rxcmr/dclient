package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.gadgets.utils.Tag
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.database.SQLItemMode
import com.fortuneteller.dclient.database.SQLItemMode.*
import com.fortuneteller.dclient.database.SQLUtils
import com.fortuneteller.dclient.database.SQLUtils.Companion.connect
import com.fortuneteller.dclient.database.SQLUtils.Companion.createDatabase
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
class JagTagCommand : Command(), SQLUtils {
  private val tags: HashSet<Tag> = HashSet()
  private val tagCache: HashSet<Tag> = HashSet()

  @Synchronized
  public override fun execute(event: CommandEvent?) {
    with(event!!) {
      val args = args?.split("\\s+".toRegex())?.toTypedArray()
      val authorID = author?.id
      val guildID = guild?.id
      val jagtag = buildParser(this)
      channel.sendTyping().queue()
      with(tags) {
        try {
          when (args!![0]) {
            "global", "g" -> {
              when (args[1]) {
                "create", "new", "add" -> {
                  if (args[2].matches(
                      "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex())
                  ) throw CommandException("Be unique, these are reserved command parameters.")
                  select(ALL)
                  val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
                  if (message.attachments.isNotEmpty()) {
                    insert(LVALUE, "${message.attachments[0].proxyUrl} $tagValue", authorID)
                  } else insert(GVALUE, args[2], tagValue, authorID)
                  select(ALL)
                  clear()
                  addAll(tagCache)
                }
                "delete", "remove" -> {
                  select(ALL)
                  if (exists(GVALUE, args[2])) delete(LVALUE, args[2], authorID)
                  else throw CommandException("Deleting something that does not exist.")
                  select(ALL)
                  clear()
                  addAll(tagCache)
                }
                "edit", "modify" -> {
                  select(ALL)
                  val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
                  update(GVALUE, args[2], tagValue, authorID)
                  select(ALL)
                  clear()
                  addAll(tagCache)
                }
                "raw" -> stream().forEachOrdered { t ->
                  val exists = exists(GVALUE, args[2])
                  try {
                    if (!exists) throw CommandException("Tag not found.")
                  } catch (e: CommandException) {
                    throw CommandException(e.message)
                  } finally {
                    if (t.tagKey == args[0] && t.guildID == "GLOBAL") reply(t.tagValue)
                  }
                }
                "cblkraw" -> stream().forEachOrdered { t ->
                  val exists = exists(GVALUE, args[2])
                  try {
                    if (!exists) throw CommandException("Tag not found.")
                  } catch (e: CommandException) {
                    throw CommandException(e.message)
                  } finally {
                    if (t.tagKey == args[0] && t.guildID == "GLOBAL") reply("```${t.tagValue}```")
                  }
                }
                else -> stream().forEachOrdered { t ->
                  val exists = exists(GVALUE, args[1])
                  try {
                    if (!exists) throw CommandException("Tag not found.")
                  } catch (e: CommandException) {
                    throw CommandException(e.message)
                  } finally {
                    if (t.tagKey == args[0] && t.guildID == guildID && exists) reply(jagtag.parse(t.tagValue))
                  }
                }
              }
            }
            "create", "new", "add" -> {
              if (isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
              if (args[1].matches(
                  "(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex())
              ) throw CommandException("Be unique, these are reserved command parameters.")
              select(ALL)
              val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
              if (message.attachments.isNotEmpty()) {
                insert(LVALUE, "${message.attachments[0].proxyUrl} $tagValue", authorID, guildID)
              } else insert(LVALUE, args[1], tagValue, authorID, guildID)
              select(ALL)
              clear()
              addAll(tagCache)
            }
            "delete", "remove" -> {
              if (isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
              select(ALL)
              if (exists(LVALUE, args[1], guildID)) delete(LVALUE, args[1], authorID, guildID)
              else throw CommandException("Deleting something that does not exist.")
              select(ALL)
              clear()
              addAll(tagCache)
            }
            "edit", "modify" -> {
              if (isFromType(ChannelType.PRIVATE)) throw CommandException("Use the global parameter.")
              select(ALL)
              val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
              update(LVALUE, args[1], tagValue, authorID, guildID)
              select(ALL)
              clear()
              addAll(tagCache)
            }
            "raw" -> stream().forEachOrdered { t ->
              if (channelType == ChannelType.PRIVATE) throw CommandException("Use the global parameter.")
              else {
                val exists = exists(LVALUE, args[1], guildID)
                try {
                  if (!exists) throw CommandException("Tag not found.")
                } catch (e: CommandException) {
                  throw CommandException(e.message)
                } finally {
                  if (t.tagKey == args[0] && t.guildID == guildID && exists) reply(t.tagValue)
                }
              }
            }
            "cblkraw" -> stream().forEachOrdered { t ->
              if (channelType == ChannelType.PRIVATE) throw CommandException("Use the global parameter.")
              else {
                val exists = exists(LVALUE, args[1], guildID)
                try {
                  if (!exists) throw CommandException("Tag not found.")
                } catch (e: CommandException) {
                  throw CommandException(e.message)
                } finally {
                  if (t.tagKey == args[0] && t.guildID == guildID && exists) reply("```${t.tagValue}```")
                }
              }

            }
            "eval" -> {
              reply("Type `!!stop` to exit.")
              val id = channel.id
              jda.addEventListener(
                object : ListenerAdapter() {
                  override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
                    with(event) {
                      if (author.isBot || author.isFake || channel.id != id) return
                      channel.sendTyping().queue()
                      val message = message.contentRaw.split("\\s+").toTypedArray()
                      val arguments = message.joinToString(" ")
                      val parser = buildParser(event)
                      if (message[0].equals("!!stop", ignoreCase = true)) jda.removeEventListener(this)
                      else channel.sendMessage(parser.parse(arguments)).queue()
                    }
                  }
                }
              )
            }
            else -> stream().forEachOrdered { t ->
              if (channelType == ChannelType.PRIVATE) throw CommandException("Use the global parameter.")
              else {
                val exists = exists(LVALUE, args[0], guildID)
                try {
                  if (!exists) throw CommandException("Tag not found.")
                } catch (e: CommandException) {
                  throw CommandException(e.message)
                } finally {
                  if (t.tagKey == args[0] && t.guildID == guildID && exists) reply(jagtag.parse(t.tagValue))
                }
              }
            }
          }
        } catch (s: SQLException) {
          reply(s.message)
          if (s.errorCode == 19) throw CommandException("Tag exists or missing parameters.")
          else throw CommandException(s.message + s.message)
        }
      }
    }
  }

  private fun buildParser(event: Any): Parser {
    if (event is CommandEvent) {
      return JagTag.newDefaultBuilder().addMethods(with(event) {
        listOf(
          with(author) {
            Method("author") { _ -> name }
            Method("mAuthor") { _ -> asMention }
          },
          with(guild) {
            Method("guild") { _ -> name }
            Method("guildID") { _ -> id }
            Method("memberCount") { _ -> memberCount.toString() }
            Method("boostCount") { _ -> boostCount.toString() }
            Method("owner") { _ -> owner?.effectiveName }
            Method("ownerID") { _ -> ownerId }
            Method("roles") { _ -> roles.stream().map(Role::getName).collect(Collectors.joining(", ")) }
            Method("randMember") { _ -> members[SecureRandom().nextInt(members.size)].effectiveName }
            Method("randChannel") { _ -> channels[SecureRandom().nextInt(channels.size)].name }
          },
          Method("strlen") { _ -> (args.split("\\s+".toRegex()).size - 1).toString() },
          Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) }
        )
      }).build()
    } else {
      event as GuildMessageReceivedEvent
      return JagTag.newDefaultBuilder().addMethods(with(event) {
        listOf(
          with(author) {
            Method("author") { _ -> name }
            Method("mAuthor") { _ -> asMention }
          },
          with(guild) {
            Method("guild") { _ -> name }
            Method("guildID") { _ -> id }
            Method("memberCount") { _ -> memberCount.toString() }
            Method("boostCount") { _ -> boostCount.toString() }
            Method("owner") { _ -> owner?.effectiveName }
            Method("ownerID") { _ -> ownerId }
            Method("roles") { _ -> roles.stream().map(Role::getName).collect(Collectors.joining(", ")) }
            Method("randMember") { _ -> members[SecureRandom().nextInt(members.size)].effectiveName }
            Method("randChannel") { _ -> channels[SecureRandom().nextInt(channels.size)].name }
          },
          Method("strlen") { _ -> (message.contentRaw.split("\\s+".toRegex()).size - 1).toString() },
          Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) }
        )
      }).build()
    }
  }

  @Synchronized
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

  @Synchronized
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

  @Synchronized
  override fun select(mode: SQLItemMode, vararg args: String?) {
    when (mode) {
      ALL -> {
        val sql = "SELECT * FROM tags"
        val connection = connect()
        val preparedStatement = connection.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery()
        tagCache.clear()
        with(resultSet) {
          while (next()) tagCache.add(Tag().set(
            getString("tagKey"),
            getString("tagValue"),
            getString("ownerID"),
            getString("guildID")
          ))
        }
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

  @Synchronized
  override fun delete(mode: SQLItemMode, vararg args: String?) {
    val sql = "DELETE FROM tags WHERE tagKey = ? AND ownerID = ? AND guildID = ?"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    with(preparedStatement) {
      when (mode) {
        LVALUE -> {
          setString(1, args[0])
          setString(2, args[1])
          setString(3, args[2])
          executeUpdate()
        }
        GVALUE -> {
          setString(1, args[0])
          setString(2, args[1])
          setString(3, "GLOBAL")
          executeUpdate()
        }
        else -> return
      }
    }
  }

  @Synchronized
  override fun update(mode: SQLItemMode, vararg args: String?) {
    val sql = "UPDATE tags SET tagValue = ? WHERE tagKey = ? AND ownerID = ? AND guildID = ?"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    with(preparedStatement) {
      when (mode) {
        LVALUE -> {
          setString(1, args[1])
          setString(2, args[0])
          setString(3, args[2])
          setString(4, args[3])
          executeUpdate()
        }
        GVALUE -> {
          setString(1, args[1])
          setString(2, args[0])
          setString(3, args[2])
          setString(4, "GLOBAL")
          executeUpdate()
        }
        else -> return
      }
    }
  }

  @Synchronized
  override fun exists(mode: SQLItemMode, vararg args: String?): Boolean {
    val sql = "SELECT EXISTS(SELECT tagKey, guildID FROM tags WHERE tagKey = ? AND guildID = ?)"
    val connection = connect()
    val preparedStatement = connection.prepareStatement(sql)
    return with(preparedStatement) {
      when (mode) {
        LVALUE -> {
          setString(1, args[0])
          setString(2, args[1])
          val resultSet = executeQuery()
          resultSet.getInt(1) >= 1
        }
        GVALUE -> {
          setString(1, args[0])
          setString(2, "GLOBAL")
          val resultSet = executeQuery()
          resultSet.getInt(1) >= 1
        }
        else -> false
      }
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