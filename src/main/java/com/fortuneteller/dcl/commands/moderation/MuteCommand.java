package com.fortuneteller.dcl.commands.moderation;
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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class MuteCommand extends Command {
  public MuteCommand() {
    name = "mute";
    help = "Mutes a user in respect to time in minutes.";
    userPermissions = new Permission[]{
      Permission.MANAGE_ROLES, Permission.MANAGE_SERVER, Permission.MESSAGE_MANAGE, Permission.MANAGE_PERMISSIONS
    };
    botPermissions = new Permission[]{
      Permission.MANAGE_ROLES, Permission.MANAGE_SERVER, Permission.MESSAGE_MANAGE, Permission.MANAGE_PERMISSIONS
    };
    arguments = "**<amount>** **<user>**";
    category = Categories.MODERATION.getCategory();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    try {
      String[] args = event.getArgs().split("\\s+");
      Role role = event.getGuild()
        .getRoles()
        .stream()
        .filter(r -> r.getPermissionsRaw() == 0x0)
        .collect(Collectors.toList())
        .get(0);

      if (event.getGuild().getRoles().contains(role)) {
        for (Member m : event.getMessage().getMentionedMembers()) {
          event.getTextChannel().putPermissionOverride(role).deny(Permission.MESSAGE_WRITE).queue();
          event.getGuild().addRoleToMember(m, role).queue();
          Executors.newSingleThreadScheduledExecutor().schedule(
            () -> {
              event.getGuild().removeRoleFromMember(m, role).queue();
              event.reply("Unmuted " + m.getAsMention() + " successfully.");
            },
            Long.parseLong(args[0]),
            TimeUnit.MINUTES
          );
        }
      }
    } catch (IndexOutOfBoundsException e) {
      createMutedRole(event);
      execute(event);
    }
  }

  private void createMutedRole(@NotNull CommandEvent event) {
    event.getGuild()
      .createRole()
      .setName("Muted")
      .setColor(0x00000)
      .setMentionable(false)
      .setPermissions((long) 0x0)
      .queue();
  }
}
