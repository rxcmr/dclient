package dcl;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import dcl.commands.utils.FleshListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.Compression;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.concurrent.Executors;

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

/**
 * @author rxcmr
 */
public class Skeleton {
   public static String prefix = "fl!", ID = "175610330217447424";

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
      (@NotNull String token,
       int shards,
       @NotNull Collection<Command> commands,
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
      Executors.newCachedThreadPool().execute(() -> {
         try { init(); } catch (LoginException l) {
            logger.error("[!!!] LoginException occurred: ", l.getCause());
            logger.warn("[!!!] Supply the .env file!");
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
         .setHttpClientBuilder(new OkHttpClient.Builder().dns(Dns.SYSTEM))
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
