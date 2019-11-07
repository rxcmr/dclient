package dcl;

import com.jagrosh.jdautilities.command.Command;
import dcl.commands.*;
import dcl.listeners.DisconnectListener;
import dcl.listeners.ExceptionListener;
import dcl.listeners.ReadyEventListener;
import dcl.listeners.ReconnectListener;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author rxcmr
 */
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
            new QueryUserCommand(), new ShardCommand(),
            new PlayTrackCommand(), new SkipTrackCommand(),
            new LeaveVoiceChannelCommand()
         )
      );
      // EventListeners
      ArrayList<Object> eventListeners = new ArrayList<>(
         Arrays.asList(
            new ReadyEventListener(), new ExceptionListener(),
            new DisconnectListener(), new ReconnectListener()
         )
      );
      // Client instance
      Skeleton skeleton = new Skeleton(token, shards, commands, eventListeners, poolSize);
      skeleton.run();
   }
}
