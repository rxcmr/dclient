package dcl.commands;

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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class QueryUserCommand extends Command {
  private EmbedBuilder embedBuilder = new EmbedBuilder();

  public QueryUserCommand() {
    name = "queryuser";
    aliases = new String[]{"userinfo"};
    cooldown = 10;
    arguments = "**<user>**";
    help = "Information about a user.";
    category = Categories.UTILITIES.getCategory();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue(
      v -> {
        Member member = event.getMessage().getMentionedMembers().isEmpty()
          ? (Member) event.getJDA().getUserById(event.getArgs())
          : event.getMessage().getMentionedMembers().get(0);
        User author = event.getAuthor();
        assert member != null;
        event.reply(buildEmbed(member, author));
        embedBuilder.clear();
      }
    );
  }

  @NotNull
  private MessageEmbed buildEmbed(@NotNull Member member, @NotNull User author) {
    User user = member.getUser();
    embedBuilder
      .setTitle("**Queried: **" + user.getName())
      .setDescription(String.format("**Member: **`%s`%nUser: `%s`", user, member))
      .setImage(user.getEffectiveAvatarUrl())
      .addField("**Avatar ID: **", user.getAvatarId(), false)
      .addField("**Avatar URL: **", user.getEffectiveAvatarUrl(), false)
      .addField("**Name: **", String.format("%s#%s", user.getName(), user.getDiscriminator()), false)
      .addField("**Nickname: **", member.getNickname() == null ? "No nickname" : member.getNickname(), false)
      .addField("**ID: **", user.getId(), false)
      .setColor(0x41 + 0x64 + 0x64 + 0x65 + 0x72)
      .setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
    return embedBuilder.build();
  }
}
