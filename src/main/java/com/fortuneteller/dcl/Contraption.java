package com.fortuneteller.dcl;

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

import com.fortuneteller.dcl.commands.utils.Categories;
import com.fortuneteller.dcl.commands.utils.Descriptions;
import com.fortuneteller.dcl.commands.utils.DirectMessage;
import com.fortuneteller.dcl.commands.utils.PilotCommandListener;
import com.fortuneteller.dcl.utils.CloudFlareDNS;
import com.fortuneteller.dcl.utils.PilotThreadFactory;
import com.fortuneteller.dcl.utils.UserAgentInterceptor;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import okhttp3.OkHttpClient;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.fortuneteller.dcl.utils.PilotUtils.error;
import static com.fortuneteller.dcl.utils.PilotUtils.info;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class Contraption extends Thread implements DirectMessage {
  public static final String ID = "175610330217447424";
  public static final String VERSION = "1.7.0l";
  private final @NotNull String token;
  private final int shards;
  private final @NotNull Collection<Command> commands;
  private final @Nullable Collection<Object> listeners;
  private String prefix;
  private static Contraption contraption;
  private ShardManager shardManager;
  private CommandClient commandClient;
  private final EmbedBuilder embedBuilder = new EmbedBuilder();

  @Contract(pure = true)
  Contraption
    (@NotNull final String token,
     @NotNull final String prefix,
     final int shards,
     @NotNull final Collection<Command> commands,
     @Nullable final Collection<Object> listeners) {
    super("Von Bolt");
    if (shards == 0) throw new IllegalArgumentException("Shards must not equal 0.");
    this.token = token;
    setPrefix(prefix);
    this.shards = shards;
    this.commands = commands;
    this.listeners = listeners;
    info("Constructor initialized.");
  }

  public static synchronized @NotNull Contraption getInstance() {
    return contraption;
  }

  private synchronized void setInstance(Contraption contraption) {
    Contraption.contraption = contraption;
  }

  public String getPrefix() {
    return prefix;
  }

  private void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  public @NotNull CommandClient getCommandClient() {
    return commandClient;
  }

  private void setCommandClient(CommandClient commandClient) {
    this.commandClient = commandClient;
  }

  public @NotNull ShardManager getShardManager() {
    return shardManager;
  }

  private void setShardManager(ShardManager shardManager) {
    this.shardManager = shardManager;
  }

  private void streamCommands(Command.Category category) {
    final var commandInvocation = new LinkedList<String>();
    final var commandInformation = new LinkedList<String>();
    final var commandContent = new DualLinkedHashBidiMap<String, String>();
    commands.stream()
      .filter(c -> c.getCategory() == category && (!c.isHidden() || c.isOwnerCommand()))
      .forEachOrdered(c -> {
        commandInvocation.add(String.format("`%s%s`", prefix, c.getName()));
        if (c.getArguments() == null && c.isGuildOnly())
          commandInformation.add(String.format("```GUILD ONLY %n - %s```", c.getHelp()));
        else if (c.isGuildOnly())
          commandInformation.add(String.format("```GUILD ONLY %n Arguments: %s%n - %s```",
            c.getArguments(), c.getHelp()));
        else if (c.getArguments() != null) commandInformation.add(String.format("```- %s```", c.getHelp()));
        else commandInformation.add(String.format("```Arguments: %s%n - %s```", c.getArguments(), c.getHelp()));
        var invokeIter = commandInvocation.iterator();
        var infoIter = commandInformation.iterator();
        while (invokeIter.hasNext() && infoIter.hasNext()) commandContent.put(invokeIter.next(), infoIter.next());
      });
    commandContent.forEach((k, v) -> embedBuilder.addField(k, v, false));
  }

  private @NotNull MessageEmbed buildHelpEmbed(@NotNull User author, @NotNull String args) {
    var categories = EnumSet.allOf(Categories.class);
    embedBuilder.setDescription(String.format("```Commands:%nPrefix: %s```", prefix));
    if (args.equalsIgnoreCase(Categories.GADGETS.getName())) {
      embedBuilder.addField(
        String.format("**%s**", Categories.GADGETS.getName()),
        String.format("**Description:** *%s* ", Descriptions.GADGETS.getDescription()),
        false
      );
      streamCommands(Categories.GADGETS.getCategory());
    } else if (args.equalsIgnoreCase(Categories.MUSIC.getName())) {
      embedBuilder.addField(
        String.format("**%s**", Categories.MUSIC.getName()),
        String.format("**Description:** *%s* ", Descriptions.MUSIC.getDescription()),
        false);
      streamCommands(Categories.MUSIC.getCategory());
    } else if (args.equalsIgnoreCase(Categories.MODERATION.getName())) {
      embedBuilder.addField(
        String.format("**%s**", Categories.MODERATION.getName()),
        String.format("**Description:** *%s* ", Descriptions.MODERATION.getDescription()),
        false
      );
      streamCommands(Categories.MODERATION.getCategory());
    } else if (args.equalsIgnoreCase(Categories.OWNER.getName()) && author.getId().equals(ID)) {
      embedBuilder.addField(
        String.format("**%s**", Categories.OWNER.getName()),
        String.format("**Description:** *%s* ", Descriptions.OWNER.getDescription()),
        false
      );
      streamCommands(Categories.OWNER.getCategory());
    } else if (args.isEmpty()) {
      categories.forEach(
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

  private void buildHelpConsumer(@NotNull CommandEvent event) {
    sendDirectMessage(
      buildHelpEmbed(event.getAuthor(), event.getArgs()),
      event.getAuthor(),
      null
    );
    embedBuilder.clear();
  }

  private @NotNull CommandClient buildCommandClient() {
    final var commandClientBuilder = new CommandClientBuilder();
    info("Building \033[1;93mCommandClient\033[0m.");
    commands.forEach(commandClientBuilder::addCommand);
    setCommandClient(commandClientBuilder
      .setOwnerId(ID)
      .setPrefix(prefix)
      .setActivity(Activity.listening("events."))
      .setStatus(OnlineStatus.DO_NOT_DISTURB)
      .setListener(new PilotCommandListener())
      .setHelpConsumer(this::buildHelpConsumer)
      .setShutdownAutomatically(true)
      .build());
    return getCommandClient();
  }

  private @NotNull ShardManager buildShardManager() throws UnknownHostException, LoginException {
    var factory = new PilotThreadFactory("Bolt Guard");
    setShardManager(new DefaultShardManagerBuilder()
      .setShardsTotal(shards)
      .setToken(token)
      .addEventListeners(buildCommandClient())
      .setCompression(Compression.ZLIB)
      .setCallbackPool(Executors.newFixedThreadPool(shards, factory), true)
      .setGatewayPool(Executors.newScheduledThreadPool(shards, factory), true)
      .setRateLimitPool(Executors.newScheduledThreadPool(shards, factory), true)
      .setHttpClientBuilder(new OkHttpClient.Builder()
        .dns(new CloudFlareDNS())
        .addInterceptor(new UserAgentInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES))
      .setUseShutdownNow(true)
      .setRelativeRateLimit(false)
      .setContextEnabled(true)
      .setChunkingFilter(ChunkingFilter.ALL)
      .addEventListeners(listeners != null ? listeners : Collections.singletonList(new DefaultListener()))
      .build());
    return getShardManager();
  }

  @Override
  public void run() {
    var exceptionThrown = false;
    try {
      info("Building \033[1;93mShardManager\033[0m.");
      shardManager = buildShardManager();
      info("Running.");
      info(String.format(shards > 1
        ? "\033[1;91m%s\033[0m shards active."
        : "\033[1;91m%s\033[0m shard active.", shards));
      commands.forEach(command -> info(String.format("\033[1;93mCommand\033[0m loaded: \033[1;92m%s\033[0m", command)));
      if (listeners == null) return;
      listeners.forEach(eventListener -> info(
        String.format("\033[1;93mEventListener\033[0m loaded: \033[1;92m%s\033[0m", eventListener)));
    } catch (LoginException l) {
      error("Invalid token.");
      exceptionThrown = true;
    } catch (IllegalArgumentException i) {
      error("\033[1;93mCommands\033[0m/\033[1;93mEventListeners\033[0m loading failed!");
      exceptionThrown = true;
    } catch (UnknownHostException u) {
      error("""
        Cannot connect to \033[1;95mDiscord API\033[0m/\033[1;95mWebSocket\033[0m, or \033[1;94mCloudFlare DNS\033[0m.
        """);
      exceptionThrown = true;
    } finally {
      if (exceptionThrown) {
        error("My disappointment is immeasurable, and my day is ruined.");
      } else {
        info("\033[1;93mContraption\033[0m instance: " + this);
        setInstance(this);
      }
    }
  }

  private static class DefaultListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
      info("Ready");
    }
  }
}
