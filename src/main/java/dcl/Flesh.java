package dcl;

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
import io.github.cdimascio.dotenv.Dotenv;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rxcmr
 */
public class Flesh {
   public Flesh() throws ReflectiveOperationException {
      String token = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load().get("TOKEN");
      int shards = 1, poolSize = 1;

      // Commands
      ArrayList<Command> commands = new ArrayList<>();
      // EventListeners
      ArrayList<Object> listeners = new ArrayList<>();

      // Get all commands and put new instances of it in the ArrayList commands
      List<Class<Command>> commandClassList =
         new ClassGraph()
            .whitelistPackagesNonRecursive("dcl.commands")
            .scan()
            .getAllClasses()
            .loadClasses(Command.class);
      for (Class<Command> c : commandClassList) commands.add(c.getDeclaredConstructor().newInstance());

      // Get all listeners and put new instances of it in the ArrayList listeners
      List<Class<ListenerAdapter>> listenerClassList =
         new ClassGraph()
            .whitelistPackagesNonRecursive("dcl.listeners")
            .scan()
            .getAllClasses()
            .loadClasses(ListenerAdapter.class);
      for (Class<ListenerAdapter> l : listenerClassList) listeners.add(l.getDeclaredConstructor().newInstance());

      // Instantiate the base class Skeleton
      assert token != null;
      Skeleton skeleton = new Skeleton(token, shards, commands, listeners, poolSize);
      skeleton.run();
   }

   public static void main(String[] args) throws Exception { new Flesh(); }
}
