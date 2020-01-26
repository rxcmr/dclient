package com.fortuneteller.dclient

import com.fortuneteller.dclient.commands.utils.Categories
import com.fortuneteller.dclient.commands.utils.Categories.*
import com.fortuneteller.dclient.commands.utils.PilotCommandListener
import com.fortuneteller.dclient.utils.CloudFlareDNS
import com.fortuneteller.dclient.utils.Colors.BLUE_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.GREEN_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.PURPLE_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.RED_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.Colors.RESET
import com.fortuneteller.dclient.utils.Colors.YELLOW_BOLD_BRIGHT
import com.fortuneteller.dclient.utils.PilotThreadFactory
import com.fortuneteller.dclient.utils.PilotUtils.error
import com.fortuneteller.dclient.utils.PilotUtils.info
import com.fortuneteller.dclient.utils.UserAgentInterceptor
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import okhttp3.OkHttpClient.Builder
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap
import java.io.File
import java.net.UnknownHostException
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

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
class Contraption(private val token: String,
                  private val prefix: String,
                  private val shards: Int,
                  private val commands: Collection<Command>,
                  private val listeners: Collection<Any>?) {

  companion object {
    lateinit var instance: Contraption private set
    lateinit var shardManager: ShardManager private set
    lateinit var commandClient: CommandClient private set
    lateinit var prefix: String private set
    const val ID: String = "175610330217447424"
    val VERSION = this::class.java.`package`.implementationVersion ?: "1.9.4l"

    fun generateClassGraph() {
      val file = File("./graphs/dclient.dot")

      fun generate() {
        ClassGraph()
          .whitelistPackages("com.fortuneteller.dclient")
          .enableAllInfo()
          .scan()
          .allClasses
          .generateGraphVizDotFile(File("graphs/dclient.dot"))
      }

      when (file.exists()) {
        true -> generate()
        false -> {
          file.parentFile.mkdirs()
          generate()
        }
      }
    }
  }

  private fun buildCommandClient() = CommandClientBuilder().let {
    fun buildHelpEmbed(author: User, args: String) = EmbedBuilder().let { e ->
      fun addCommandInformation(c: Command, l: LinkedList<String>) {
        if (c.arguments == null && c.isGuildOnly) l.add("```GUILD ONLY \n - ${c.help}```")
        else if (c.isGuildOnly) l.add("```GUILD ONLY %n Arguments: ${c.arguments}\n - ${c.help}```")
        else if (c.arguments != null) l.add("```- ${c.help}```")
        else l.add("```Arguments: ${c.arguments}\n - ${c.help}```")
      }

      fun categoryEmbed(category: Categories) {
        fun streamCommands(category: Command.Category) {
          val commandInvocation = LinkedList<String>()
          val commandInformation = LinkedList<String>()
          val commandContent = DualLinkedHashBidiMap<String, String>()
          commands.stream()
            .filter { c -> c.category == category && (!c.isHidden || c.isOwnerCommand) }
            .forEachOrdered { c ->
              commandInvocation.add("`$prefix${c.name}`")
              addCommandInformation(c, commandInformation)
              val invokeIter = commandInvocation.iterator()
              val infoIter = commandInformation.iterator()
              while (invokeIter.hasNext() && infoIter.hasNext()) commandContent[invokeIter.next()] = infoIter.next()
            }
          commandContent.forEach { (k, v) -> e.addField(k, v, false) }
        }

        fun addHeader(name: String, description: String) =
          e.addField("**$name**", "**Description:** *$description* ", false)

        addHeader(category.name, category.description)
        streamCommands(category.category)
      }

      val categories = EnumSet.allOf(Categories::class.java)
      e.setDescription("```Prefix: $prefix```")
      with(args) {
        when {
          equals(GADGETS.name, true) -> categoryEmbed(GADGETS)
          equals(MUSIC.name, true) -> categoryEmbed(MUSIC)
          equals(MODERATION.name, true) -> categoryEmbed(MODERATION)
          equals(OWNER.name, true) -> categoryEmbed(OWNER)
          equals(STATS.name, true) -> categoryEmbed(STATS)
          else -> {
            categories.forEach { c ->
              e.addField(
                "**Category: ${c.name}**",
                "```py\n${prefix}help ${c.name.toLowerCase()}\n```",
                false)
            }
          }
        }
      }
      e.setColor(0xd32ce6).setFooter("requested by: ${author.name}", author.avatarUrl).build()
    }

    info("Building ${YELLOW_BOLD_BRIGHT}CommandClient$RESET")
    commands.forEach { c -> it.addCommand(c) }
    commandClient = it
      .setOwnerId(ID)
      .setPrefix(prefix)
      .setActivity(Activity.listening("events."))
      .setStatus(OnlineStatus.DO_NOT_DISTURB)
      .setListener(PilotCommandListener())
      .setHelpConsumer { e -> e.replyInDm(buildHelpEmbed(e.author, e.args))
      }
      .useHelpBuilder(false)
      .setShutdownAutomatically(true)
      .build()
    commandClient
  }

  private fun buildShardManager() = PilotThreadFactory("Bolt Guard").let {
    DefaultShardManagerBuilder()
      .setShardsTotal(shards)
      .setToken(token)
      .addEventListeners(buildCommandClient())
      .setCompression(Compression.ZLIB)
      .setCallbackPool(Executors.newFixedThreadPool(shards, it), true)
      .setGatewayPool(Executors.newScheduledThreadPool(shards, it), true)
      .setRateLimitPool(Executors.newScheduledThreadPool(shards, it), true)
      .setHttpClientBuilder(Builder()
        .dns(CloudFlareDNS())
        .addInterceptor(UserAgentInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES))
      .setUseShutdownNow(true)
      .setRelativeRateLimit(false)
      .setContextEnabled(true)
      .setChunkingFilter(ChunkingFilter.NONE)
      .addEventListeners(listeners ?: listOf<Any>(DefaultListener()))
      .build()
  }

  private fun retryPrompt(): Unit = Scanner(System.`in`).use {
    info("Retry connection? [y/n]: ")
    when (it.next()) {
      "y" -> launch()
      "n" -> exitProcess(-9)
      else -> exitProcess(-9)
    }
  }

  fun launch() = AtomicBoolean(false).let { ex ->
    val pattern = Pattern.compile("([A-Z])\\w+")
    try {
      info("Building ${YELLOW_BOLD_BRIGHT}ShardManager$RESET")
      shardManager = buildShardManager()
      info("Running.")
      info(if (shards > 1) "$RED_BOLD_BRIGHT$shards$RESET shards active."
      else "$RED_BOLD_BRIGHT$shards$RESET shard active.")
      commands.forEach { c ->
        pattern.matcher(c.toString()).let { p ->
          while (p.find())
            info("${YELLOW_BOLD_BRIGHT}Command$RESET loaded: $GREEN_BOLD_BRIGHT${p.group(0)}$RESET")
        }
      }
      if (listeners == null) return
      listeners.forEach { l ->
        pattern.matcher(l.toString()).let { p ->
          while (p.find())
            info("${YELLOW_BOLD_BRIGHT}EventListener$RESET loaded: $GREEN_BOLD_BRIGHT${p.group(0)}$RESET")
        }
      }
    } catch (l: LoginException) {
      error("Invalid token or time out.")
      ex.set(true)
    } catch (i: IllegalArgumentException) {
      error("${YELLOW_BOLD_BRIGHT}Commands$RESET/${YELLOW_BOLD_BRIGHT}EventListeners$RESET loading failed!")
      ex.set(true)
    } catch (u: UnknownHostException) {
      ex.set(true)
      error("Cannot connect to ${PURPLE_BOLD_BRIGHT}Discord API$RESET/" +
        "${PURPLE_BOLD_BRIGHT}WebSocket$RESET, or ${BLUE_BOLD_BRIGHT}CloudFlare DNS$RESET.")
    } finally {
      when (!ex.get()) {
        true -> {
          instance = this
          Companion.prefix = prefix
          info("Finished initializing in ${Duration.between(Pilot.initTime, Instant.now()).toMillis()} ms")
        }
        false -> {
          error("My disappointment is immeasurable, and my day is ruined.")
          retryPrompt()
        }
      }
    }
  }

  private class DefaultListener : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) = info("Ready!")
  }

  init {
    if (shards <= 0) throw IllegalArgumentException("Shards should be a non-zero positive integer.")
    info("Initialized.")
  }
}