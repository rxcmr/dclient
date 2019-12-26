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
import net.dv8tion.jda.api.utils.cache.CacheFlag
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
                  val prefix: String,
                  private val shards: Int,
                  private val commands: Collection<Command>,
                  private val listeners: Collection<Any>?) : Thread("Von Bolt"), DirectMessage {

  companion object {
    lateinit var instance: Contraption
    const val id: String = "175610330217447424"
    const val version = "1.8.1l"
  }

  lateinit var shardManager: ShardManager
  lateinit var commandClient: CommandClient
  private val embedBuilder = EmbedBuilder()

  init {
    if (shards <= 0) throw IllegalArgumentException()
    info("Initialized.")
  }

  private fun streamCommands(category: Command.Category) {
    val commandInvocation = LinkedList<String>()
    val commandInformation = LinkedList<String>()
    val commandContent = DualLinkedHashBidiMap<String, String>()
    commands.stream()
      .filter { c -> c.category == category && (!c.isHidden || c.isOwnerCommand) }
      .forEachOrdered { c ->
        run {
          commandInvocation.add(String.format("`%s%s`", prefix, c.name))
          if (c.arguments == null && c.isGuildOnly)
            commandInformation.add(String.format("```GUILD ONLY %n - %s```", c.help))
          else if (c.isGuildOnly)
            commandInformation.add(String.format("```GUILD ONLY %n Arguments: %s%n - %s```", c.arguments, c.help))
          else if (c.arguments != null)
            commandInformation.add(String.format("```- %s```", c.help))
          else
            commandInformation.add(String.format("```Arguments: %s%n - %s```", c.arguments, c.help))
          val invokeIter = commandInvocation.iterator()
          val infoIter = commandInformation.iterator()
          while (invokeIter.hasNext() && infoIter.hasNext()) commandContent.put(invokeIter.next(), infoIter.next())
        }
      }
    commandContent.forEach { (k, v) -> embedBuilder.addField(k, v, false) }
  }

  private fun addHeader(name: String, description: String) {
    embedBuilder.addField(
      String.format("**%s**", name),
      String.format("**Description:** *%s* ", description),
      false
    )
  }

  private fun buildHelpEmbed(author: User, args: String): MessageEmbed {
    val categories = EnumSet.allOf(Categories::class.java)
    embedBuilder.setDescription(String.format("```Prefix: %s```", prefix))
    if (args.equals(GADGETS.name, ignoreCase = true)) {
      addHeader(GADGETS.name, GADGETS.description)
      streamCommands(GADGETS.category)
    } else if (args.equals(MUSIC.name, ignoreCase = true)) {
      addHeader(MUSIC.name, MUSIC.description)
      streamCommands(MUSIC.category)
    } else if (args.equals(MODERATION.name, ignoreCase = true)) {
      addHeader(MODERATION.name, MODERATION.description)
      streamCommands(MODERATION.category)
    } else if (args.equals(OWNER.name, ignoreCase = true)) {
      addHeader(OWNER.name, OWNER.description)
      streamCommands(OWNER.category)
    } else if (args.isEmpty()) {
      categories.forEach { c ->
        embedBuilder.addField(
          "**Category: " + c.name + "**",
          String.format("```py%n%shelp %s%n```", prefix, c.name.toLowerCase()),
          false)
      }
    }
    embedBuilder
      .setColor(0xd32ce6)
      .setFooter("requested by: " + author.name, Objects.requireNonNull(author.avatarUrl))
    return embedBuilder.build()
  }

  private fun buildHelpConsumer(event: CommandEvent) {
    sendDirectMessage(buildHelpEmbed(event.author, event.args), event.author, null)
    embedBuilder.clear()
  }

  private fun buildCommandClient(): CommandClient {
    val commandClientBuilder = CommandClientBuilder()
    info("Building \u001b[1;93mCommandClient\u001b[0m.")
    commands.forEach { c -> commandClientBuilder.addCommand(c) }
    commandClient = commandClientBuilder
      .setOwnerId(id.toString())
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
      .setDisabledCacheFlags(EnumSet.of(CacheFlag.VOICE_STATE))
      .setChunkingFilter(ChunkingFilter.NONE)
      .addEventListeners(listeners ?: listOf<Any>(DefaultListener()))
      .build()
    return shardManager
  }

  override fun run() {
    var exceptionThrown = false
    val pattern = Pattern.compile("([A-Z])\\w+")
    try {
      info("Building \u001b[1;93mShardManager\u001b[0m.")
      shardManager = buildShardManager()
      info("Running.")
      info(String.format(
        if (shards > 1) "\u001b[1;91m%s\u001b[0m shards active."
        else "\u001b[1;91m%s\u001b[0m shard active.", shards))
      commands.forEach { c ->
        run {
          val matcher = pattern.matcher(c.toString())
          while (matcher.find()) info(String.format("\u001b[1;93mCommand\u001b[0m loaded: \u001b[1;92m%s\u001b[0m",
            matcher.group(0)))
        }
      }
      if (listeners == null) return
      listeners.forEach { l ->
        run {
          val matcher = pattern.matcher(l.toString())
          while (matcher.find()) info(String.format("\u001b[1;93mEventListener\u001b[0m loaded: \u001b[1;92m%s\u001b[0m",
            matcher.group(0)))
        }
      }
    } catch (l: LoginException) {

      error("Invalid token.")
      exceptionThrown = true
    } catch (i: IllegalArgumentException) {

      error("\u001B[1;93mCommands\u001B[0m/\u001B[1;93mEventListeners\u001B[0m loading failed!")
      exceptionThrown = true
    } catch (u: UnknownHostException) {
      exceptionThrown = true
      error("Cannot connect to \u001B[1;95mDiscord API\u001B[0m/" +
        "\u001B[1;95mWebSocket\u001B[0m, or \u001B[1;94mCloudFlare DNS\u001B[0m.")
    } finally {
      if (!exceptionThrown) {
        info("\u001B[1;93mContraption\u001B[0m instance: " + toString())
        instance = this
      } else {
        error("My disappointment is immeasurable, and my day is ruined.")
      }
    }
  }

  override fun toString(): String {
    return "type: \u001b[1;93m" + Thread::class.simpleName + "\u001b[0m name: " + name
  }

  private class DefaultListener : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
      info("Ready!")
    }
  }
}