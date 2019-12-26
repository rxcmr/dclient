package com.fortuneteller.dclient.commands.music.utils

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.jetbrains.annotations.Contract
import java.util.*

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
 */ /**
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
class TrackLoader {
  private val playerManager: AudioPlayerManager
  private val musicManagers: MutableMap<Long, GuildMusicManager>

  fun loadAndPlay(channel: TextChannel, trackURL: String) {
    val musicManager = getGuildAudioPlayer(channel.guild)
    playerManager.loadItemOrdered(musicManager, trackURL, object : AudioLoadResultHandler {
      override fun trackLoaded(track: AudioTrack) {
        channel.sendMessageFormat("Adding to queue: **%s**", track.info.title).queue()
        play(channel.guild, musicManager, track)
      }

      override fun playlistLoaded(playlist: AudioPlaylist) {
        var firstTrack = playlist.selectedTrack
        if (firstTrack == null) firstTrack = playlist.tracks[0]
        channel.sendMessageFormat("Adding to queue: **%s** *(first track of playlist %s)*",
          firstTrack?.info?.title,
          playlist.name).queue()
        play(channel.guild, musicManager, firstTrack)
      }

      override fun noMatches() {
        channel.sendMessage("Nothing found by: $trackURL.").queue()
      }

      override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage("Could not play: " + exception.message).queue()
      }
    })
  }

  fun displayQueue(channel: TextChannel): String {
    val musicManager = getGuildAudioPlayer(channel.guild)
    return java.lang.String.join("\n", musicManager.scheduler.trackList)
  }

  fun getScheduler(channel: TextChannel): TrackScheduler {
    return getGuildAudioPlayer(channel.guild).scheduler
  }

  private fun play(guild: Guild, musicManager: GuildMusicManager, track: AudioTrack?) {
    connectToFirstVoiceChannel(guild.audioManager)
    musicManager.scheduler.queue(track!!)
  }

  fun skipTrack(channel: TextChannel) {
    val musicManager = getGuildAudioPlayer(channel.guild)
    musicManager.scheduler.nextTrack()
    channel.sendMessage("Skipped.").queue()
  }

  @Synchronized
  private fun getGuildAudioPlayer(guild: Guild): GuildMusicManager {
    val guildId = guild.id.toLong()
    val musicManager = musicManagers.computeIfAbsent(guildId) { GuildMusicManager(playerManager) }
    guild.audioManager.sendingHandler = musicManager.sendHandler
    return musicManager
  }

  @Contract("_ -> param1")
  private fun registerSourceManagers(manager: AudioPlayerManager): AudioPlayerManager {
    val youtubeAudioSourceManager = YoutubeAudioSourceManager()
    youtubeAudioSourceManager.configureRequests { config: RequestConfig? -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build() }
    manager.registerSourceManager(youtubeAudioSourceManager)
    manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
    manager.registerSourceManager(TwitchStreamAudioSourceManager())
    manager.registerSourceManager(BandcampAudioSourceManager())
    manager.registerSourceManager(VimeoAudioSourceManager())
    manager.registerSourceManager(BeamAudioSourceManager())
    manager.registerSourceManager(LocalAudioSourceManager())
    manager.registerSourceManager(HttpAudioSourceManager())
    return manager
  }

  companion object {
    val instance = TrackLoader()

    private fun connectToFirstVoiceChannel(audioManager: AudioManager) {
      if (!audioManager.isConnected && !audioManager.isAttemptingToConnect) audioManager.guild
        .voiceChannels.stream().filter { obj: VoiceChannel? -> Objects.nonNull(obj) }.findFirst().ifPresent { channel: VoiceChannel? -> audioManager.openAudioConnection(channel) }
    }
  }

  init {
    playerManager = registerSourceManagers(DefaultAudioPlayerManager())
    musicManagers = HashMap()
  }
}