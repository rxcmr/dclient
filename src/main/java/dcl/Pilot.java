package dcl;

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
import io.github.cdimascio.dotenv.Dotenv;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.LinkedList;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class Pilot {
  final String token;
  final String delimiter;
  final int shards;

  public Pilot(String token, String delimiter, int shards) throws ReflectiveOperationException {
    this.token = token;
    this.delimiter = delimiter;
    this.shards = shards;
    final LinkedList<Command> commands = new LinkedList<>();
    final LinkedList<Object> listeners = new LinkedList<>();

    for (Class<Command> c : new ClassGraph()
      .blacklistPackages("dcl.commands.utils")
      .whitelistPackages("dcl.commands.*")
      .scan()
      .getAllClasses()
      .loadClasses(Command.class))
      commands.add(c.getDeclaredConstructor().newInstance());

    for (Class<ListenerAdapter> l : new ClassGraph()
      .whitelistPackagesNonRecursive("dcl.listeners")
      .scan()
      .getAllClasses()
      .loadClasses(ListenerAdapter.class))
      listeners.add(l.getDeclaredConstructor().newInstance());

    new Machina(token, delimiter, shards, commands, listeners).start();
  }

  public static void main(String[] args) throws Exception {
    final String mainToken = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load().get("TOKEN");
    final String mainDelimiter = "fl!";
    //final String subToken = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load().get("SUBTOKEN");
    //final String subDelimiter = "rg!";
    final int shards = 2;
    new Pilot(mainToken, mainDelimiter, shards);
    //new Pilot(subToken, subDelimiter, shards);
  }
}
