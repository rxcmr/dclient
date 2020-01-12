package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.gadgets.utils.JagTagTable
import com.fortuneteller.dclient.commands.gadgets.utils.Tag
import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.database.SQLItemMode
import com.fortuneteller.dclient.database.SQLItemMode.*
import com.fortuneteller.dclient.database.SQLUtils
import com.fortuneteller.dclient.database.SQLUtils.Companion.transact
import com.jagrosh.jagtag.JagTag
import com.jagrosh.jagtag.Method
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.*
import org.sqlite.SQLiteException
import java.nio.file.Files
import java.nio.file.Path
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.LinkedHashSet

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
  private val tags = LinkedHashSet<Tag>()
  private val tagCache = LinkedHashSet<Tag>()

  @Synchronized
  public override fun execute(event: CommandEvent): Unit = with(event) {
    val args = args?.split("\\s+".toRegex())?.toTypedArray()
    val authorID = author?.id!!
    val guildID = guild?.id!!
    val jagtag = buildParser(this)
    channel.sendTyping().queue()
    with(tags) {
      try {
        when (args!![0]) {
          "global", "g" -> {
            when (args[1]) {
              "create", "new", "add" -> {
                if (args[2].matches("(global|g|create|new|add|delete|remove|edit|modify|raw|cblkraw)".toRegex()))
                  throw CommandException("Be unique, these are reserved command parameters.")
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
                  if (t.tagKey == args[2] && t.guildID == "GLOBAL" && exists) reply(t.tagValue)
                }
              }
              "cblkraw" -> stream().forEachOrdered { t ->
                val exists = exists(GVALUE, args[2])
                try {
                  if (!exists) throw CommandException("Tag not found.")
                } catch (e: CommandException) {
                  throw CommandException(e.message)
                } finally {
                  if (t.tagKey == args[2] && t.guildID == "GLOBAL" && exists) reply("```${t.tagValue}```")
                }
              }
              else -> stream().forEachOrdered { t ->
                val exists = exists(GVALUE, args[1])
                try {
                  if (!exists) throw CommandException("Tag not found.")
                } catch (e: CommandException) {
                  throw CommandException(e.message)
                } finally {
                  if (t.tagKey == args[1] && t.guildID == "GLOBAL" && exists) reply(jagtag?.parse(t.tagValue))
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
                  event.let {
                    if (it.author.isBot || it.author.isFake || it.channel.id != id) return
                    it.channel.sendTyping().queue()
                    val message = it.message.contentRaw.split("\\s+").toTypedArray()
                    val arguments = message.joinToString(" ")
                    if (message[0].equals("!!stop", ignoreCase = true)) jda.removeEventListener(this)
                    else it.channel.sendMessage(buildParser(it)?.parse(arguments)!!).queue()
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
                if (t.tagKey == args[0] && t.guildID == guildID && exists) reply(jagtag?.parse(t.tagValue))
              }
            }
          }
        }
      } catch (s: SQLiteException) {
        if (s.errorCode == 19) throw CommandException("Tag exists or missing parameters.")
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

  override fun createTable() = transact {
    SchemaUtils.createMissingTablesAndColumns(JagTagTable)
    exec("PRAGMA auto_vacuum = FULL;")!!
  }

  override fun insert(mode: SQLItemMode, vararg args: String) = transact {
    when (mode) {
      LVALUE -> JagTagTable.insert {
        it[tagKey] = args[0]
        it[tagValue] = args[1]
        it[ownerID] = args[2]
        it[guildID] = args[3]
      }
      GVALUE -> JagTagTable.insert {
        it[tagKey] = args[0]
        it[tagValue] = args[1]
        it[ownerID] = args[2]
        it[guildID] = "GLOBAL"
      }
      else -> return@transact
    }
  }

  override fun select(mode: SQLItemMode, vararg args: String) = transact {
    JagTagTable.selectAll().forEach {
      tagCache.add(Tag().set(
        it[JagTagTable.tagKey],
        it[JagTagTable.tagValue],
        it[JagTagTable.ownerID],
        it[JagTagTable.guildID]
      ))
    }
  }

  override fun delete(mode: SQLItemMode, vararg args: String) = transact {
    when (mode) {
      LVALUE -> JagTagTable.deleteWhere {
        JagTagTable.tagKey eq args[0]
        JagTagTable.ownerID eq args[1]
        JagTagTable.guildID eq args[2]
      }
      GVALUE -> JagTagTable.deleteWhere {
        JagTagTable.tagKey eq args[0]
        JagTagTable.ownerID eq args[1]
        JagTagTable.guildID eq "GLOBAL"
      }
      else -> return@transact
    }
  }


  override fun update(mode: SQLItemMode, vararg args: String) = transact {
    when (mode) {
      LVALUE -> JagTagTable.update({
        JagTagTable.tagKey eq args[0]
        JagTagTable.ownerID eq args[2]
        JagTagTable.guildID eq args[3]
      }) {
        it[tagValue] = args[1]
      }
      GVALUE -> JagTagTable.update({
        JagTagTable.tagKey eq args[0]
        JagTagTable.ownerID eq args[2]
        JagTagTable.guildID eq "GLOBAL"
      }) {
        it[tagValue] = args[1]
      }
      else -> return@transact
    }
  }

  override fun exists(mode: SQLItemMode, vararg args: String) = transact {
    when (mode) {
      LVALUE -> JagTagTable.slice(JagTagTable.tagKey, JagTagTable.guildID).select {
        JagTagTable.tagKey eq args[0]
        JagTagTable.guildID eq args[1]
      }.count() > 0
      GVALUE -> JagTagTable.slice(JagTagTable.tagKey, JagTagTable.guildID).select {
        JagTagTable.tagKey eq args[0]
        JagTagTable.guildID eq "GLOBAL"
      }.count() > 0
      else -> false
    }
  }

  init {
    name = "jagtag"
    aliases = arrayOf("tag", "t")
    category = Categories.GADGETS.category
    arguments = "**<modifier>** **<name>** **<content>**"
    help = "JagTag like in Spectra"
    val path = Path.of("./sqlite/PilotDB.sqlite")
    if (Files.exists(path)) {
      select(ALL)
      tags.addAll(tagCache)
    } else {
      try {
        Files.createDirectories(path)
      } finally {
        createTable()
      }
    }
  }
}