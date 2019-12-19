package com.fortuneteller.dcl.commands.gadgets;

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

import com.fortuneteller.dcl.commands.utils.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class QueryUserCommand extends Command {
  private EmbedBuilder embedBuilder = new EmbedBuilder();

  public QueryUserCommand() {
    name = "queryuser";
    aliases = new String[]{"userinfo"};
    cooldown = 10;
    arguments = "**<user>**";
    help = "Information about a user.";
    category = Categories.GADGETS.getCategory();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue();
    var member = event.getMessage().getMentionedMembers().isEmpty()
      ? (Member) event.getJDA().getUserById(event.getArgs())
      : event.getMessage().getMentionedMembers().get(0);
    var author = event.getAuthor();
    assert member != null;
    event.reply(buildEmbed(member, author));
    embedBuilder.clear();
  }

  @NotNull
  private MessageEmbed buildEmbed(@NotNull Member member, @NotNull User author) {
    var user = member.getUser();
    var permissions = member.getPermissions().stream()
      .map(Permission::getName).collect(Collectors.joining(", "));
    var roles = member.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "));
    var clientType = member.getActiveClients().stream()
      .map(ClientType::getKey).collect(Collectors.joining(", "));
    embedBuilder
      .setTitle("**Queried: **" + user.getName())
      .setDescription(String.format("**Member: **`%s`%n**User: **`%s`", user, member))
      .setImage(user.getEffectiveAvatarUrl())
      .addField("**Avatar ID: **", user.getAvatarId(), false)
      .addField("**Avatar URL: **", user.getEffectiveAvatarUrl(), false)
      .addField("**Name: **", String.format("%s#%s", user.getName(), user.getDiscriminator()), false)
      .addField("**Nickname: **", member.getNickname() == null ? "No nickname" : member.getNickname(), false)
      .addField("**ID: **", user.getId(), false)
      .addField("**Discord Join Date: **", user.getTimeCreated().toString(), false)
      .addField("**Guild Join Date: **", member.getTimeJoined().toString(), false)
      .addField("**Status: **", member.getOnlineStatus().getKey(), false)
      .addField("**Permissions: **", permissions, false)
      .addField("**Roles: **", member.getRoles().isEmpty() ? "None" : roles, false)
      .addField("**Type: **", clientType, false)
      .setColor(0x41 + 0x64 + 0x64 + 0x65 + 0x72)
      .setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
    return embedBuilder.build();
  }
}
