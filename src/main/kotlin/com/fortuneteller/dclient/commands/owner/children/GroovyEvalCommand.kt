package com.fortuneteller.dclient.commands.owner.children

import com.fortuneteller.dclient.commands.utils.Categories
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import groovy.lang.GroovyShell
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

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
class GroovyEvalCommand : Command() {
  private val imports: String
  private val embedBuilder = EmbedBuilder()

  override fun execute(event: CommandEvent): Unit = with(event) {
    val input = args.replace("(```[a-z]*)".toRegex(), "")
    try {
      with(GroovyShell()) {
        setProperty("args", args)
        setProperty("event", this)
        setProperty("message", message)
        setProperty("channel", channel)
        setProperty("jda", jda)
        setProperty("guild", guild)
        setProperty("member", member)
        setProperty("user", member?.user)
        reply(buildEmbed(evaluate("$imports\n$input"), input))
        embedBuilder.clear()
      }
    } catch (t: Throwable) {
      reply(exceptionEmbed(t, input))
      embedBuilder.clear()
    }
  }

  private fun buildEmbed(output: Any?, args: String) = with(embedBuilder) {
    when (output) {
      null -> {
        setTitle("```Finished execution.```")
        setDescription("**Command:** ```groovy\n$args\n```")
        build()
      }
      else -> {
        setTitle("```Finished execution.```")
        setDescription("**Command:** ```groovy\n$args\n```")
        addField("**Output:** ", "```$output```", false)
        build()
      }
    }
  }

  private fun exceptionEmbed(t: Throwable, args: String): MessageEmbed {
    val exceptionName = t.javaClass.canonicalName.split("\\.".toRegex())
    val javadoc = "https://docs.oracle.com/en/java/javase/13/docs/api/java.base/" +
      "${exceptionName[0]}/${exceptionName[1]}/${exceptionName[2]}.html"

    return embedBuilder
      .setTitle("```${t.javaClass.simpleName}```",
        if (exceptionName[0].equals("java", true)) javadoc
        else null)
      .setDescription("**Command:** ```groovy\n$args\n```")
      .addField("**Stack Trace:**", "```$t```", false)
      .setColor(Color.RED)
      .build()
  }

  init {
    name = "groovy"
    ownerCommand = true
    help = "A Groovy evaluator using GroovyShell"
    arguments = "**<code>**"
    hidden = true
    category = Categories.OWNER.category
    //language=groovy
    imports = """
      import java.io.*
      import java.lang.*
      import java.util.*
      import java.util.concurrent.*
      import net.dv8tion.jda.api.*
      import net.dv8tion.jda.api.entities.*
      import net.dv8tion.jda.api.managers.*
      import net.dv8tion.jda.api.utils.*
      import com.fortuneteller.dclient.commands.gadgets.*
      import com.fortuneteller.dclient.commands.owner.*
      import com.fortuneteller.dclient.commands.music.*
      import com.fortuneteller.dclient.commands.moderation.*
      import com.fortuneteller.dclient.listeners.*
      import com.fortuneteller.dclient.commands.utils.*
      import com.fortuneteller.dclient.utils.*
      """
  }
}