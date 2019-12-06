package dcl.music;

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

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class Loader {
  private final AudioPlayerManager playerManager;
  private final Map<Long, MusicManager> musicManagers;

  public Loader() {
    playerManager = registerSourceManagers(new DefaultAudioPlayerManager());
    musicManagers = new HashMap<>();
  }

  private static void connectToFirstVoiceChannel(@NotNull AudioManager audioManager) {
    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
        audioManager.openAudioConnection(voiceChannel);
        break;
      }
    }
  }

  public void loadAndPlay(@NotNull final TextChannel channel, final String trackUrl) {
    MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessageFormat("Adding to queue: *%s*", track.getInfo().title).queue();
        play(channel.getGuild(), musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();
        if (firstTrack == null) firstTrack = playlist.getTracks().get(0);
        channel.sendMessageFormat("Adding to queue: *%s* *(first track of playlist %s)*",
          firstTrack.getInfo().title,
          playlist.getName()).queue();
        play(channel.getGuild(), musicManager, firstTrack);
      }

      @Override
      public void noMatches() {
        channel.sendMessage("Nothing found by: " + trackUrl + ".").queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  private void play(@NotNull Guild guild, @NotNull MusicManager musicManager, AudioTrack track) {
    connectToFirstVoiceChannel(guild.getAudioManager());
    musicManager.scheduler.queue(track);
  }

  public void skipTrack(@NotNull TextChannel channel) {
    MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    musicManager.scheduler.nextTrack();
    channel.sendMessage("Skipped.").queue();
  }

  @NotNull
  private synchronized MusicManager getGuildAudioPlayer(@NotNull Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    MusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new MusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }
    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
    return musicManager;
  }

  @NotNull
  @Contract("_ -> param1")
  private AudioPlayerManager registerSourceManagers(@NotNull AudioPlayerManager manager) {
    YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
    youtubeAudioSourceManager.configureRequests(
      config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
    );
    manager.registerSourceManager(youtubeAudioSourceManager);
    manager.registerSourceManager(new TwitchStreamAudioSourceManager());
    manager.registerSourceManager(new BandcampAudioSourceManager());
    manager.registerSourceManager(new VimeoAudioSourceManager());
    manager.registerSourceManager(new BeamAudioSourceManager());
    manager.registerSourceManager(new LocalAudioSourceManager());
    manager.registerSourceManager(new HttpAudioSourceManager());
    return manager;
  }
}
