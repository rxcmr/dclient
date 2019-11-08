package dcl;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.*;
import dcl.commands.listener.FleshListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.Compression;
import org.jetbrains.annotations.*;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.concurrent.*;

/**
 * @author rxcmr
 */
public class Skeleton {
   public static String prefix = "fl!";
   public static String ID = "175610330217447424";

   private Logger logger = (Logger) LoggerFactory.getLogger(Skeleton.class);
   private DefaultShardManagerBuilder managerBuilder = new DefaultShardManagerBuilder();
   private CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
   private CommandClient commandClient;
   private String token;
   private int shards;
   private Collection<Command> commands;
   private @Nullable Collection<Object> listeners;
   private int poolSize;

   @Contract(pure = true)
   Skeleton
      (String token,
       int shards,
       Collection<Command> commands,
       @Nullable Collection<Object> listeners,
       int poolSize) {
      this.token = token;
      this.shards = shards;
      this.commands = commands;
      this.listeners = listeners;
      this.poolSize = poolSize;
      logger.info("[!] Constructor initialized");
   }

   private void init() throws LoginException {
      buildCommandClient();
      logger.info("[#] Building JDA v4.0.0");
      buildShardManager();
      logger.info("[#] JDA Running");
      logger.info(String.format(shards == 1 ? "[#] %s shard active" : "[#] %s shards active", shards));
      commands.forEach(command -> logger.info(String.format("[#] Command loaded: %s", command)));
      if (!(listeners == null))
         listeners.forEach(eventListener -> logger.info("[#] EventListener loaded: " + eventListener));
   }

   void run() {
      ExecutorService service = Executors.newCachedThreadPool();
      service.execute(() -> {
         try {
            init();
         } catch (LoginException l) {
            logger.error("[!!!] LoginException occurred: ", l.getCause());
         }
      });
   }

   private void buildShardManager() throws LoginException {
      managerBuilder
         .setShardsTotal(shards)
         .setToken(token)
         .addEventListeners(commandClient)
         .setCallbackPool(Executors.newCachedThreadPool(), true)
         .setGatewayPool(Executors.newScheduledThreadPool(poolSize), true)
         .setRateLimitPool(Executors.newScheduledThreadPool(poolSize), true)
         .setCompression(Compression.ZLIB)
         .setUseShutdownNow(true)
         .setRelativeRateLimit(false)
         .setContextEnabled(true);
      if (listeners != null) managerBuilder.addEventListeners(listeners);
      managerBuilder.build();
   }

   private void buildCommandClient() {
      commandClientBuilder
         .setOwnerId(ID)
         .setPrefix(prefix)
         .setActivity(Activity.listening("events."))
         .setStatus(OnlineStatus.DO_NOT_DISTURB)
         .setListener(new FleshListener())
         .useHelpBuilder(true)
         .setShutdownAutomatically(true);
      commands.forEach(command -> commandClientBuilder.addCommand(command));
      commandClient = commandClientBuilder.build();
   }
}
