package com.fortuneteller.dclient.commands.gadgets

import com.fortuneteller.dclient.commands.utils.Categories
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.util.stream.Collectors

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
 */ /**
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
class QueryUserCommand : Command() {
  override fun execute(event: CommandEvent) = with(event) {
    channel.sendTyping().queue()
    val member = if (message.mentionedMembers.isEmpty()) guild.getMemberById(args) else message.mentionedMembers[0]
    reply(buildEmbed(member!!, author))
  }

  private fun buildEmbed(member: Member, author: User) = with(member) {
    user.let {
      val permissions = permissions.stream().map { p -> p.getName() }.collect(Collectors.joining(", "))
      val roles = roles.stream().map { r -> r.name }.collect(Collectors.joining(", "))
      val clientType = activeClients.stream().map { c -> c.key }.collect(Collectors.joining(", "))
      EmbedBuilder()
        .setTitle("**Queried: **" + it.name)
        .setDescription(String.format("**Member: **`%s`%n**User: **`%s`", it, member))
        .setImage(it.effectiveAvatarUrl)
        .addField("**Avatar ID: **", it.avatarId, false)
        .addField("**Avatar URL: **", it.effectiveAvatarUrl, false)
        .addField("**Name: **", "${it.name}#${it.discriminator}", false)
        .addField("**Nickname: **", nickname ?: "No nickname", false)
        .addField("**ID: **", it.id, false)
        .addField("**Discord Join Date: **", it.timeCreated.toString(), false)
        .addField("**Guild Join Date: **", timeJoined.toString(), false)
        .addField("**Status: **", onlineStatus.key, false)
        .addField("**Permissions: **", permissions, false)
        .addField("**Roles: **", if (roles.isEmpty()) "None" else roles, false)
        .addField("**Type: **", clientType, false)
        .setColor(0x41 + 0x64 + 0x64 + 0x65 + 0x72)
        .setFooter("requested by: ${author.name}", author.avatarUrl)
        .build()
    }
  }

  init {
    name = "queryuser"
    aliases = arrayOf("userinfo")
    cooldown = 10
    arguments = "**<user>**"
    help = "Information about a user."
    category = Categories.GADGETS.category
  }
}