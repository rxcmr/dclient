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
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.commands.utils.Descriptions;
import dcl.commands.utils.DirectMessage;
import dcl.commands.utils.FleshListener;
import dcl.utils.CloudFlareDNS;
import dcl.utils.GLogger;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.Compression;
import okhttp3.OkHttpClient;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class Skeleton {
  public static String prefix = "fl!", ID = "175610330217447424";
  private DirectMessage dm = (a, b, c) -> b.openPrivateChannel().queue(
    a instanceof MessageEmbed
      ? (c == null ? d -> d.sendMessage((MessageEmbed) a).queue() : d -> d.sendMessage(a + c).queue())
      : (c == null ? d -> d.sendMessage(a.toString()).queue() : d -> d.sendMessage(a + c).queue())
  );
  private ShardManager shardManager;
  private static CommandClient commandClient;
  private final EmbedBuilder embedBuilder = new EmbedBuilder();
  private final DefaultShardManagerBuilder managerBuilder = new DefaultShardManagerBuilder();
  private final CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
  private final String token;
  private final DualLinkedHashBidiMap<String, String> commandContent = new DualLinkedHashBidiMap<>();
  private final List<String> commandInvocation = new LinkedList<>();
  private final List<String> commandInformation = new LinkedList<>();
  private final Collection<Command> commands;
  private final @Nullable Collection<Object> listeners;
  private final int poolSize;
  private final int threads;
  private final int shards;

  @Contract(pure = true)
  Skeleton
    (@NotNull String token,
     int shards,
     @NotNull Collection<Command> commands,
     @Nullable Collection<Object> listeners,
     int poolSize,
     int threads) {
    if (shards == 0 || poolSize == 0 || threads == 0)
      throw new IllegalArgumentException("Shards or pool size must not equal 0.");
    this.token = token;
    this.shards = shards;
    this.commands = commands;
    this.listeners = listeners;
    this.poolSize = poolSize;
    this.threads = threads;
    GLogger.info("[!] Constructor initialized");
  }

  @SuppressWarnings("unused")
  public ShardManager getInstance() {
    return shardManager;
  }

  public static CommandClient getCommandClient() {
    return commandClient;
  }

  private void helpConsumer(@NotNull CommandEvent event) {
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
    embedBuilder.setDescription(String.format("```Commands:%nPrefix: %s```", prefix));
    if (args.equalsIgnoreCase(Categories.UTILITIES.getName())) {
      embedBuilder.addField(
        String.format("**%s: **", Categories.UTILITIES.getName()),
        String.format("**Description:** *%s* ", Descriptions.UTILITIES.getDescription()),
        false
      );
      streamCommands(Categories.UTILITIES.getCategory());
    } else if (args.equalsIgnoreCase(Categories.MUSIC.getName())) {
      embedBuilder.addField(
        String.format("**%s: **", Categories.MUSIC.getName()),
        String.format("**Description:** *%s* ", Descriptions.MUSIC.getDescription()),
        false);
      streamCommands(Categories.MUSIC.getCategory());
    } else if (args.equalsIgnoreCase(Categories.MODERATION.getName())) {
      embedBuilder.addField(
        String.format("**%s: **", Categories.MODERATION.getName()),
        String.format("**Description:** *%s* ", Descriptions.MODERATION.getDescription()),
        false
      );
      streamCommands(Categories.MODERATION.getCategory());
    } else if (args.equalsIgnoreCase(Categories.OWNER.getName()) && author.getId().equals(ID)) {
      embedBuilder.addField(
        String.format("**%s: **", Categories.OWNER.getName()),
        String.format("**Description:** *%s* ", Descriptions.OWNER.getDescription()),
        false
      );
      streamCommands(Categories.OWNER.getCategory());
    } else if (args.isEmpty()) {
      Arrays.stream(categories).forEachOrdered(
        category -> embedBuilder.addField(
          "**Category: " + category.getName() + "**",
          String.format("```py%n%shelp %s%n```", prefix, category.getName().toLowerCase()),
          false
        )
      );
    }
    embedBuilder
      .setColor(0xd32ce6)
      .setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
    return embedBuilder.build();
  }

  private void streamCommands(Command.Category category) {
    commandInformation.clear();
    commandContent.clear();
    commands.stream().filter(c -> c.getCategory() == category).forEachOrdered(c -> {
      commandInvocation.add(String.format("`%s%s`", prefix, c.getName()));
      commandInformation.add(
        c.getArguments() == null
          ? String.format("``` - %s```", c.getHelp())
          : String.format("``` Arguments: %s%n - %s```", c.getArguments(), c.getHelp())
      );
      Iterator<String> invokeIter = commandInvocation.iterator();
      Iterator<String> infoIter = commandInformation.iterator();
      while (invokeIter.hasNext() && infoIter.hasNext()) commandContent.put(invokeIter.next(), infoIter.next());
    });
    commandContent.forEach((k, v) -> embedBuilder.addField(k, v, false));
  }

  private void init() throws Exception {
    GLogger.info("[#] Building JDA v4.0.0");
    buildShardManager();
    GLogger.info("[#] JDA Running");
    GLogger.info(String.format(shards > 1 ? "[#] %s shards active" : "[#] %s shard active", shards));
    commands.forEach(command -> GLogger.info(String.format("[#] Command loaded: %s", command)));
    if (listeners == null) return;
    listeners.forEach(eventListener -> GLogger.info("[#] EventListener loaded: " + eventListener));
  }

  void run() {
    Executors.newCachedThreadPool().execute(() -> {
      try {
        init();
      } catch (Exception e) {
        GLogger.error("[!!!] LoginException occurred: ", e);
        if (e instanceof IllegalArgumentException)
          GLogger.warn("[!!!] Commands / EventListeners loading failed!");
        GLogger.warn("[!!!] Cannot connect to REST API, CloudFlare DNS, or invalid token.");
      }
    });
  }

  private void buildShardManager() throws Exception {
    buildCommandClient();
    managerBuilder
      .setShardsTotal(shards)
      .setToken(token)
      .addEventListeners(commandClient)
      .setCallbackPool(Executors.newFixedThreadPool(threads), true)
      .setGatewayPool(Executors.newScheduledThreadPool(poolSize), true)
      .setRateLimitPool(Executors.newScheduledThreadPool(poolSize), true)
      .setCompression(Compression.ZLIB)
      .setHttpClientBuilder(new OkHttpClient.Builder().dns(new CloudFlareDNS()))
      .setUseShutdownNow(true)
      .setRelativeRateLimit(false)
      .setContextEnabled(true);
    if (listeners != null) managerBuilder.addEventListeners(listeners);
    else managerBuilder.addEventListeners(new DefaultListener());
    shardManager = managerBuilder.build();
  }

  private void buildCommandClient() {
    GLogger.info("[#] Building CommandClient");
    commandClientBuilder
      .setOwnerId(ID)
      .setPrefix(prefix)
      .setActivity(Activity.listening("events."))
      .setStatus(OnlineStatus.IDLE)
      .setListener(new FleshListener())
      .setHelpConsumer(this::helpConsumer)
      .setShutdownAutomatically(true);
    commands.forEach(commandClientBuilder::addCommand);
    commandClient = commandClientBuilder.build();
  }

  private static class DefaultListener extends ListenerAdapter {
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
      GLogger.info("[#] Ready");
    }
  }
}
