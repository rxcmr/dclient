package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.gadgets.utils.JagTagTable
import com.fortuneteller.dclient.commands.gadgets.utils.Tag
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.database.SQLItemMode
import com.fortuneteller.dclient.database.SQLItemMode.ALL
import com.fortuneteller.dclient.database.SQLItemMode.GVALUE
import com.fortuneteller.dclient.database.SQLItemMode.LVALUE
import com.fortuneteller.dclient.database.SQLUtils
import com.fortuneteller.dclient.database.SQLUtils.Companion.transact
import com.fortuneteller.dclient.utils.ExMessage
import com.fortuneteller.dclient.utils.loadEnv
import com.jagrosh.jagtag.JagTag
import com.jagrosh.jagtag.Method
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.sqlite.SQLiteException
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.LinkedList
import java.util.stream.Collectors

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
class JagTagCommand : Command(), SQLUtils {
  private val tags = LinkedHashSet<Tag>()
  private val tagCache = LinkedHashSet<Tag>()
  private val db = loadEnv("DB")

  public override fun execute(event: CommandEvent): Unit = with(event) {
    val args = args?.split("\\s+".toRegex())?.toTypedArray()!!
    val authorID = author?.id!!
    val guildID = guild?.id!!
    val jagtag = buildParser(this)
    channel.sendTyping().queue()
    with(tags) {
      try {
        when (args[0]) {
          "global", "g" -> when (args[1]) {
            "create", "new", "add" -> {
              clearAndSelect()
              if (args[2].matches("(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex()))
                throw CommandException(ExMessage.JT_RESERVED)
              val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
              if (message.attachments.isNotEmpty())
                insert(LVALUE, "${message.attachments[0].proxyUrl} $tagValue", authorID)
              else insert(GVALUE, args[2], tagValue, authorID)
            }
            "delete", "remove" -> {
              clearAndSelect()
              if (exists(GVALUE, args[2])) delete(LVALUE, args[2], authorID)
              else throw CommandException(ExMessage.JT_DELETE_NOTHING)
            }
            "edit", "modify" -> {
              clearAndSelect()
              val tagValue = Arrays.stream(args).skip(3).collect(Collectors.joining(" "))
              update(GVALUE, args[2], tagValue, authorID)
            }
            "raw" -> stream().forEachOrdered { t ->
              if (!exists(GVALUE, args[2])) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[2] && t.guildID == "GLOBAL") reply(t.tagValue)
            }
            "cblkraw" -> stream().forEachOrdered { t ->
              if (!exists(GVALUE, args[2])) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[2] && t.guildID == "GLOBAL") reply("```${t.tagValue}```")
            }
            "info", "i" -> stream().forEachOrdered { t ->
              if (!exists(GVALUE, args[2])) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[2]) reply("Owned by: **${jda.getUserById(t.ownerID)?.asTag}** (${t.ownerID})" +
                " in guild **${jda.getGuildById(t.guildID)?.name}** (${t.guildID})")
            }
            else -> stream().forEachOrdered { t ->
              if (!exists(GVALUE, args[1])) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[1] && t.guildID == "GLOBAL") reply(jagtag?.parse(t.tagValue))
            }
          }
          "create", "new", "add" -> {
            clearAndSelect()
            if (isFromType(ChannelType.PRIVATE)) throw CommandException(ExMessage.JT_GLOBAL)
            if (args[1].matches("(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex())
            ) throw CommandException(ExMessage.JT_RESERVED)
            val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
            if (message.attachments.isNotEmpty())
              insert(LVALUE, "${message.attachments[0].proxyUrl} $tagValue", authorID, guildID)
            else insert(LVALUE, args[1], tagValue, authorID, guildID)
          }
          "delete", "remove" -> {
            clearAndSelect()
            if (isFromType(ChannelType.PRIVATE)) throw CommandException(ExMessage.JT_GLOBAL)
            if (exists(LVALUE, args[1], guildID)) delete(LVALUE, args[1], authorID, guildID)
            else throw CommandException(ExMessage.JT_DELETE_NOTHING)
          }
          "edit", "modify" -> {
            clearAndSelect()
            if (isFromType(ChannelType.PRIVATE)) throw CommandException(ExMessage.JT_GLOBAL)
            val tagValue = Arrays.stream(args).skip(2).collect(Collectors.joining(" "))
            update(LVALUE, args[1], tagValue, authorID, guildID)
          }
          "raw" -> stream().forEachOrdered { t ->
            if (channelType == ChannelType.PRIVATE) throw CommandException(ExMessage.JT_GLOBAL)
            else {
              if (!exists(LVALUE, args[1], guildID)) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[0] && t.guildID == guildID) reply(t.tagValue)
            }
          }
          "cblkraw" -> stream().forEachOrdered { t ->
            if (channelType == ChannelType.PRIVATE) throw CommandException(ExMessage.JT_GLOBAL)
            else {
              if (!exists(LVALUE, args[1], guildID)) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[0] && t.guildID == guildID) reply("```${t.tagValue}```")
            }

          }
          "eval" -> {
            reply("Type `!!stop` to exit.")
            val id = channel.id
            jda.addEventListener(object : ListenerAdapter() {
              override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
                event.let {
                  if (it.author.isBot || it.author.isFake || it.channel.id != id) return
                  it.channel.sendTyping().queue()
                  val message = it.message.contentRaw.split("\\s+").toTypedArray()
                  val arguments = message.joinToString(" ")
                  if (message[0].equals("!!stop", ignoreCase = true)) jda.removeEventListener(this)
                  else it.channel.sendMessage(buildParser(it)?.parse(arguments)!!).queue()
                }
              }
            })
          }
          "info", "i" -> stream().forEachOrdered { t ->
            if (!exists(LVALUE, args[1], guildID)) throw CommandException(ExMessage.JT_NOT_FOUND)
            else if (t.tagKey == args[1]) reply("Owned by: **${jda.getUserById(t.ownerID)?.asTag}** (${t.ownerID})" +
              " in guild **${jda.getGuildById(t.guildID)?.name}** (${t.guildID})")
          }
          else -> stream().forEachOrdered { t ->
            if (channelType == ChannelType.PRIVATE) throw CommandException(ExMessage.JT_GLOBAL)
            else {
              if (!exists(LVALUE, args[0], guildID)) throw CommandException(ExMessage.JT_NOT_FOUND)
              else if (t.tagKey == args[0] && t.guildID == guildID) reply(jagtag?.parse(t.tagValue))
            }
          }
        }
      } catch (s: SQLiteException) {
        if (s.errorCode == 19) throw CommandException(ExMessage.JT_EXISTS_OR_MISSING)
        else throw CommandException(s.message)
      }
    }
  }

  private fun buildParser(event: Any) = JagTag.newDefaultBuilder()?.let {
    val methods = if (event is CommandEvent) {
      LinkedList<Method>().apply {
        with(event) {
          with(author) {
            add(Method("author") { _ -> name })
            add(Method("mAuthor") { _ -> asMention })
          }
          with(guild) {
            add(Method("guild") { _ -> name })
            add(Method("guildID") { _ -> id })
            add(Method("memberCount") { _ -> memberCount.toString() })
            add(Method("boostCount") { _ -> boostCount.toString() })
            add(Method("owner") { _ -> owner?.effectiveName })
            add(Method("ownerID") { _ -> ownerId })
            add(Method("roles") { _ ->
              roles.stream().map(Role::getName).collect(Collectors.joining(", "))
            })
            add(Method("randMember") { _ -> members[SecureRandom().nextInt(members.size)].effectiveName })
            add(Method("randChannel") { _ -> channels[SecureRandom().nextInt(channels.size)].name })
          }
          add(Method("strlen") { _ -> (args.split("\\s+".toRegex()).size - 1).toString() })
          add(Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) })
        }
      }
    } else {
      event as GuildMessageReceivedEvent
      LinkedList<Method>().apply {
        with(event) {
          with(author) {
            add(Method("author") { _ -> name })
            add(Method("mAuthor") { _ -> asMention })
          }
          with(guild) {
            add(Method("guild") { _ -> name })
            add(Method("guildID") { _ -> id })
            add(Method("memberCount") { _ -> memberCount.toString() })
            add(Method("boostCount") { _ -> boostCount.toString() })
            add(Method("owner") { _ -> owner?.effectiveName })
            add(Method("ownerID") { _ -> ownerId })
            add(Method("roles") { _ ->
              roles.stream().map(Role::getName).collect(Collectors.joining(", "))
            })
            SecureRandom().let {
              add(Method("randMember") { _ -> members[it.nextInt(members.size)].effectiveName })
              add(Method("randChannel") { _ -> channels[it.nextInt(channels.size)].name })
            }
          }
          add(Method("strlen") { _ -> (message.contentRaw.split("\\s+".toRegex()).size).toString() })
          add(Method("date") { _ -> SimpleDateFormat("MM-dd-yyyy").format(Date()) })
        }
      }
    }
    it.addMethods(methods).build()
  }

  private fun clearAndSelect() {
    tags.clear()
    tagCache.clear()
    select(ALL)
    tags += tagCache
    tagCache.clear()
  }

  override fun createTable() = transact(db) {
    if (JagTagTable.exists()) clearAndSelect()
    else {
      if (this@JagTagCommand.db == "sqlite") /* language=SQLite */ exec("PRAGMA auto_vacuum = FULL;")
      SchemaUtils.createMissingTablesAndColumns(JagTagTable)
    }
  }

  override fun insert(mode: SQLItemMode, vararg args: String): Unit = transact(db) {
    JagTagTable.insert {
      it[tagKey] = if (args[0].isEmpty()) throw CommandException(ExMessage.JT_KEY_EMPTY) else args[0]
      it[tagValue] = if (args[1].isEmpty()) throw CommandException(ExMessage.JT_VAL_EMPTY) else args[1]
      it[ownerID] = args[2]
      it[guildID] = if (mode == GVALUE) "GLOBAL" else args[3]
    }
  }

  override fun select(mode: SQLItemMode, vararg args: String) = transact(db) {
    JagTagTable.selectAll().forEach {
      tagCache += Tag().set(
        it[JagTagTable.tagKey],
        it[JagTagTable.tagValue],
        it[JagTagTable.ownerID],
        it[JagTagTable.guildID]
      )
    }
  }

  override fun delete(mode: SQLItemMode, vararg args: String): Unit = transact(db) {
    JagTagTable.deleteWhere {
      JagTagTable.tagKey eq args[0]
      JagTagTable.ownerID eq args[1]
      JagTagTable.guildID eq if (mode == GVALUE) "GLOBAL" else args[2]
    }
  }

  override fun update(mode: SQLItemMode, vararg args: String): Unit = transact(db) {
    JagTagTable.update({
      JagTagTable.tagKey eq args[0]
      JagTagTable.ownerID eq args[2]
      JagTagTable.guildID eq if (mode == GVALUE) "GLOBAL" else args[3]
    }) {
      it[tagValue] = if (args[1].isEmpty()) throw CommandException(ExMessage.JT_VAL_EMPTY) else args[1]
    }
  }

  override fun exists(mode: SQLItemMode, vararg args: String) = transact(db) {
    JagTagTable.slice(JagTagTable.tagKey, JagTagTable.guildID).select {
      JagTagTable.tagKey eq args[0]
      JagTagTable.guildID eq if (mode == GVALUE) "GLOBAL" else args[1]
    }.count() > 0
  }

  init {
    name = "jagtag"
    aliases = arrayOf("tag", "t")
    category = Categories.GADGETS.category
    arguments = "**<modifier>** **<name>** **<content>**"
    help = "JagTag like in Spectra"
    if (db == "sqlite") Path.of("./sqlite/PilotDB.sqlite").let {
      if (Files.exists(it)) {
        select(ALL)
        tags += tagCache
      } else Files.createDirectories(it.parent)
    }
    createTable()
  }
}
