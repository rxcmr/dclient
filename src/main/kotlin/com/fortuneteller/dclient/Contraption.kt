package com.fortuneteller.dclient

import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.Categories.*
import com.fortuneteller.dclient.commands.utils.DirectMessage
import com.fortuneteller.dclient.commands.utils.PilotCommandListener
import com.fortuneteller.dclient.utils.CloudFlareDNS
import com.fortuneteller.dclient.utils.PilotThreadFactory
import com.fortuneteller.dclient.utils.PilotUtils.Companion.error
import com.fortuneteller.dclient.utils.PilotUtils.Companion.info
import com.fortuneteller.dclient.utils.UserAgentInterceptor
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import okhttp3.OkHttpClient
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.security.auth.login.LoginException

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

class Contraption(private val token: String,
                  var prefix: String,
                  private val shards: Int,
                  private val commands: Collection<Command>,
                  private val listeners: Collection<Any>?) : Thread("Von Bolt"), DirectMessage {

  companion object {
    lateinit var instance: Contraption
    lateinit var prefix: String
    const val ID: String = "175610330217447424"
    const val VERSION = "1.8.1l"
    const val esc = "\u001B"
  }

  lateinit var shardManager: ShardManager
  lateinit var commandClient: CommandClient
  private val embedBuilder = EmbedBuilder()

  private fun streamCommands(category: Command.Category) {
    val commandInvocation = LinkedList<String>()
    val commandInformation = LinkedList<String>()
    val commandContent = DualLinkedHashBidiMap<String, String>()
    commands.stream()
      .filter { c -> c.category == category && (!c.isHidden || c.isOwnerCommand) }
      .forEachOrdered { c ->
        commandInvocation.add("`$prefix${c.name}`")
        with(commandInformation) {
          if (c.arguments == null && c.isGuildOnly) add("```GUILD ONLY \n - ${c.help}```")
          else if (c.isGuildOnly) add("```GUILD ONLY %n Arguments: ${c.arguments}\n - ${c.help}```")
          else if (c.arguments != null) add("```- ${c.help}```")
          else add("```Arguments: ${c.arguments}\n - ${c.help}```")
        }
        val invokeIter = commandInvocation.iterator()
        val infoIter = commandInformation.iterator()
        while (invokeIter.hasNext() && infoIter.hasNext()) commandContent[invokeIter.next()] = infoIter.next()
      }
    commandContent.forEach { (k, v) -> embedBuilder.addField(k, v, false) }
  }

  private fun addHeader(name: String, description: String) {
    embedBuilder.addField("**$name**", "**Description:** *$description* ", false)
  }

  private fun buildHelpEmbed(author: User, args: String): MessageEmbed {
    val categories = EnumSet.allOf(Categories::class.java)
    embedBuilder.setDescription("```Prefix: $prefix```")
    with(args) {
      when {
        equals(GADGETS.name, ignoreCase = true) -> {
          addHeader(GADGETS.name, GADGETS.description)
          streamCommands(GADGETS.category)
        }
        equals(MUSIC.name, ignoreCase = true) -> {
          addHeader(MUSIC.name, MUSIC.description)
          streamCommands(MUSIC.category)
        }
        equals(MODERATION.name, ignoreCase = true) -> {
          addHeader(MODERATION.name, MODERATION.description)
          streamCommands(MODERATION.category)
        }
        equals(OWNER.name, ignoreCase = true) -> {
          addHeader(OWNER.name, OWNER.description)
          streamCommands(OWNER.category)
        }
        else -> {
          categories.forEach { c ->
            embedBuilder.addField(
              "**Category: ${c.name}**",
              "```py\n${prefix}help ${c.name.toLowerCase()}\n```",
              false)
          }
        }
      }
    }
    embedBuilder
      .setColor(0xd32ce6)
      .setFooter("requested by: ${author.name}", Objects.requireNonNull(author.avatarUrl))
    return embedBuilder.build()
  }

  private fun buildHelpConsumer(event: CommandEvent) {
    sendDirectMessage(buildHelpEmbed(event.author, event.args), event.author, null)
    embedBuilder.clear()
  }

  private fun buildCommandClient(): CommandClient {
    val commandClientBuilder = CommandClientBuilder()
    info("Building $esc[1;93mCommandClient$esc[0m.")
    commands.forEach { c -> commandClientBuilder.addCommand(c) }
    commandClient = commandClientBuilder
      .setOwnerId(ID)
      .setPrefix(prefix)
      .setActivity(Activity.listening("events."))
      .setStatus(OnlineStatus.DO_NOT_DISTURB)
      .setListener(PilotCommandListener())
      .setHelpConsumer { e -> buildHelpConsumer(e) }
      .setShutdownAutomatically(true)
      .build()
    return commandClient
  }

  private fun buildShardManager(): ShardManager {
    val factory = PilotThreadFactory("Bolt Guard")
    shardManager = DefaultShardManagerBuilder()
      .setShardsTotal(shards)
      .setToken(token)
      .addEventListeners(buildCommandClient())
      .setCompression(Compression.ZLIB)
      .setCallbackPool(Executors.newFixedThreadPool(shards, factory), true)
      .setGatewayPool(Executors.newScheduledThreadPool(shards, factory), true)
      .setRateLimitPool(Executors.newScheduledThreadPool(shards, factory), true)
      .setHttpClientBuilder(OkHttpClient.Builder()
        .dns(CloudFlareDNS())
        .addInterceptor(UserAgentInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES))
      .setUseShutdownNow(true)
      .setRelativeRateLimit(false)
      .setContextEnabled(true)
      .setChunkingFilter(ChunkingFilter.NONE)
      .addEventListeners(listeners ?: listOf<Any>(DefaultListener()))
      .build()
    return shardManager
  }

  override fun run() {
    var exceptionThrown = false
    val pattern = Pattern.compile("([A-Z])\\w+")
    try {
      info("Building $esc[1;93mShardManager$esc[0m.")
      shardManager = buildShardManager()
      info("Running.")
      info(if (shards > 1) "$esc[1;91m$shards$esc[0m shards active." else "$esc[1;91m$shards$esc[0m shard active.")
      commands.forEach { c ->
        val matcher = pattern.matcher(c.toString())
        while (matcher.find()) info(String.format("$esc[1;93mCommand$esc[0m loaded: $esc[1;92m%s$esc[0m",
          matcher.group(0)))
      }
      if (listeners == null) return
      listeners.forEach { l ->
        val matcher = pattern.matcher(l.toString())
        while (matcher.find())
          info(String.format("$esc[1;93mEventListener$esc[0m loaded: $esc[1;92m%s$esc[0m", matcher.group(0)))
      }
    } catch (l: LoginException) {
      error("Invalid token.")
      exceptionThrown = true
    } catch (i: IllegalArgumentException) {
      error("$esc[1;93mCommands$esc[0m/$esc[1;93mEventListeners$esc[0m loading failed!")
      exceptionThrown = true
    } catch (u: UnknownHostException) {
      exceptionThrown = true
      error("Cannot connect to $esc[1;95mDiscord API$esc[0m/" +
        "$esc[1;95mWebSocket$esc[0m, or $esc[1;94mCloudFlare DNS$esc[0m.")
    } finally {
      if (!exceptionThrown) {
        info("$esc[1;93mContraption$esc[0m instance: " + toString())
        instance = this
        prefix = instance.prefix
      } else {
        error("My disappointment is immeasurable, and my day is ruined.")
      }
    }
  }

  override fun toString(): String {
    return "type: $esc[1;93m" + Thread::class.simpleName + "$esc[0m name: " + name
  }

  private class DefaultListener : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
      info("Ready!")
    }
  }

  init {
    if (shards <= 0) throw IllegalArgumentException("Shards should be a non-zero positive integer.")
    info("Initialized.")
  }
}