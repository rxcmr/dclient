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

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.commands.utils.DirectMessage;
import dcl.commands.utils.FleshListener;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.Compression;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author rxcmr
 */
public class Skeleton {
   public static String prefix = "fl!", ID = "175610330217447424";

   private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
      a instanceof MessageEmbed ?
         (c == null ? d -> d.sendMessage((MessageEmbed) a).queue() : d -> d.sendMessage(a + c).queue())
         : (c == null ? d -> d.sendMessage(a.toString()).queue() : d -> d.sendMessage(a + c).queue())
   );
   private BidiMap<String, String> aboutCommand = new DualLinkedHashBidiMap<>();
   private List<String> commandInvocation = new LinkedList<>();
   private List<String> commandInfo = new LinkedList<>();
   private EmbedBuilder embedBuilder = new EmbedBuilder();
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

   private void consumeHelp(@NotNull CommandEvent event) {
      dm.send(
         buildHelpEmbed(event.getAuthor(), event.getArgs()),
         event.getAuthor(),
         null
      );
      embedBuilder.clear();
   }

   @NotNull
   private MessageEmbed buildHelpEmbed(@NotNull User author, @NotNull String args) {
      Field[] categories = new ClassGraph()
         .whitelistPackages("dcl.commands.utils")
         .whitelistClasses("Categories")
         .scan()
         .getAllClasses()
         .loadClasses()
         .get(0)
         .getFields();
      embedBuilder.setTitle("```Commands: ```").setDescription("```Prefix: " + prefix + "```");
      if (args.equalsIgnoreCase("utilities")) {
         // Utilities category
         embedBuilder.addField(
            "```" + Categories.utilities.getName() + ": ```",
            "`General utilities`",
            false
         );
         streamCommands(Categories.utilities);
      } else if (args.equalsIgnoreCase("music")) {
         // Music category
         embedBuilder.addField(
            "```" + Categories.music.getName() + ": ```",
            "`Music related commands`",
            false);
         streamCommands(Categories.music);
      } else if (args.equalsIgnoreCase("moderation")) {
         // Moderation category
         embedBuilder.addField(
            "```" + Categories.moderation.getName() + ": ```",
            "`Moderation commands`",
            false
         );
         streamCommands(Categories.moderation);
      } else if (args.equalsIgnoreCase("ownerOnly") && author.getId().equals(ID)) {
         embedBuilder.addField(
            "```" + Categories.ownerOnly.getName() + ": ```",
            "`Owner-only utilities`",
            false
         );
         streamCommands(Categories.ownerOnly);
      } else if (args.isEmpty()) {
         Arrays.stream(categories).forEachOrdered(
            f -> embedBuilder.addField(prefix + "help", "`" + f.getName() + "`", false)
         );
      }
      embedBuilder
         .setColor(0x41 + 0x64 + 0x64 + 0x65 + 0x72)
         .setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
      return embedBuilder.build();
   }


   private void streamCommands(Command.Category category) {
      commandInfo.clear();
      aboutCommand.clear();
      for (Command c : commands) {
         if (c.getCategory() == category) {
            commandInvocation.add("```" + prefix + c.getName() + "```");
            commandInfo.add(
               c.getArguments() == null ? "```" + " - " + c.getHelp() + "```"
                  : "```" + " " + c.getArguments() + " - " + c.getHelp() + "```"
            );
            Iterator<String> invokeIter = commandInvocation.iterator();
            Iterator<String> infoIter = commandInfo.iterator();
            while (invokeIter.hasNext() && infoIter.hasNext()) aboutCommand.put(invokeIter.next(), infoIter.next());
         }
      }
      aboutCommand.forEach((k, v) -> embedBuilder.addField(k, v, false));
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
            logger.error("[!!!] LoginException occurred: ", l);
            logger.warn("[!!!] Cannot connect to REST API or invalid token.");
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
         .setHelpConsumer(this::consumeHelp)
         .setShutdownAutomatically(true);
      commands.forEach(command -> commandClientBuilder.addCommand(command));
      commandClient = commandClientBuilder.build();
   }
}
