package dcl;

import com.jagrosh.jdautilities.command.Command;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author rxcmr
 */
public class Flesh {
   public Flesh() throws ReflectiveOperationException {
      Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
      String token = dotenv.get("TOKEN");
      int shards = 1, poolSize = 1;
      // Commands
      ArrayList<Command> commands = new ArrayList<>();
      // EventListeners
      ArrayList<Object> listeners = new ArrayList<>();
      // Get all commands and put new instances of it in the ArrayList
      Reflections commandReflections = new Reflections("dcl.commands");
      Set<Class<? extends Command>> commandSet = commandReflections.getSubTypesOf(Command.class);
      for (Class<? extends Command> c : commandSet) commands.add(c.getDeclaredConstructor().newInstance());
      // Get all listeners and put new instances of it in the ArrayList
      Reflections listenerReflections = new Reflections("dcl.listeners");
      Set<Class<? extends ListenerAdapter>> listenerSet = listenerReflections.getSubTypesOf(ListenerAdapter.class);
      for (Class<? extends ListenerAdapter> l : listenerSet) listeners.add(l.getDeclaredConstructor().newInstance());
      // Client instance
      Skeleton skeleton = new Skeleton(token, shards, commands, listeners, poolSize);
      skeleton.run();
   }

   public static void main(String[] args) throws ReflectiveOperationException {
      new Flesh();
   }
}
