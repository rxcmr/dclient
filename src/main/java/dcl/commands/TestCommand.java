package dcl.commands;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com>.
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
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class TestCommand extends Command {
  public TestCommand() {
    name = "test";
    aliases = new String[]{"try"};
    help = "???";
    ownerCommand = true;
    category = Categories.Owner;
    hidden = true;
  }

  @Override
  @SuppressWarnings("ALL")
  protected void execute(@NotNull CommandEvent event) {
    //String[] args = event.getArgs().split("\\s+");
    //event.getChannel().sendTyping().queue();
    //Arrays.stream(args).forEachOrdered(event::reply);
    List<Role> roles = event.getGuild().getRoles();
    List<Role> emptyRoles = new LinkedList<>();
    event.reply("Roles: " + roles);
    event.reply("");
    // this should return an empty List
    event.reply("List 1: " + event.getGuild().getMembersWithRoles(roles));
    event.reply("");
    // ??? returns all members for some reason
    event.reply("List 2: " + event.getGuild().getMembersWithRoles());
    event.reply("");
    // this should throw an exception
    event.reply("List 3: " + event.getGuild().getMembersWithRoles((Collection<Role>) null));
    event.reply("");
    // this throws the same exception as passing null
    event.reply("List 4: " + event.getGuild().getMembersWithRoles(emptyRoles));
  }
}
