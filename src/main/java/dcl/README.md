#`Skeleton`

To use, instantiate Skeleton and pass in 5 parameters.

- `String token`
- `int shards`
- `Collection<Commands> commands`
- `Collection<Object> eventListeners`
- `int poolSize`

```java
package dcl;

import com.jagrosh.jdautilities.command.Command;
import dcl.commands.*;
import dcl.listeners.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Arrays;

public class Flesh {
   public static void main(String[] args) {
      Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
      String token = dotenv.get("TOKEN");
      int shards = 1, poolSize = 1;
      // Commands
      ArrayList<Command> commands = new ArrayList<>(
         Arrays.asList(
            new TestCommand(), new ShutdownCommand(),
            new LatencyCommand(), new PurgeCommand(),
            new QueryUserCommand(), new ShardCommand()
         )
      );
      // EventListeners
      ArrayList<Object> eventListeners = new ArrayList<>(
         Arrays.asList(
            new ReadyEventListener(), new ExceptionListener()
         )
      );
      // Client instance
      Skeleton skeleton = new Skeleton(token, shards, commands, eventListeners, poolSize);
      skeleton.run();
   }
}
```