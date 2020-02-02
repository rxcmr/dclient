package com.fortuneteller.dclient.commands.music.utils

import com.fortuneteller.dclient.commands.utils.CommandException
import com.fortuneteller.dclient.utils.ExMessage
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
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.jetbrains.annotations.Contract
import java.util.*

/*
 * Copyright 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *      Copyright (C) 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
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

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class TrackLoader {
  private val playerManager: AudioPlayerManager
  private val musicManagers: MutableMap<Long, GuildMusicManager>

  companion object {
    val instance = TrackLoader()

    val Guild.musicManager: GuildMusicManager
      get() = instance.musicManagers.computeIfAbsent(id.toLong()) { GuildMusicManager(instance.playerManager) }.also {
        audioManager.sendingHandler = it.sendingHandler
      }

    private fun connectToVoiceChannel(member: Member, audioManager: AudioManager) {
      with(audioManager) { when {
        !isConnected && !isAttemptingToConnect -> guild.voiceChannels.stream()
          .filter { it.members.stream().anyMatch { m -> m.id == member.id } }.findFirst()
          .ifPresent { openAudioConnection(it) }
        guild.voiceChannels.stream().noneMatch { it.members.stream().anyMatch { m -> m.id == member.id } } ->
          throw CommandException(ExMessage.M_NOT_JOINED)
      }}
    }
  }

  fun loadAndPlay(channel: TextChannel, member: Member, trackURL: String): Unit = with(channel) {
    playerManager.loadItemOrdered(guild.musicManager, trackURL, object : AudioLoadResultHandler {
      override fun trackLoaded(track: AudioTrack) = with(track) {
        sendMessage("Adding track to queue: **${info.title}**").queue()
        play(member, this)
      }

      override fun playlistLoaded(playlist: AudioPlaylist) = with(playlist) {
        sendMessage("Adding playlist to queue: **$name**").queue()
        when {
          tracks.size == 1 || isSearchResult -> trackLoaded(selectedTrack ?: tracks[0])
          selectedTrack != null -> trackLoaded(selectedTrack)
          else -> tracks.forEach { if (it == null) return else trackLoaded(it) }
        }
      }

      override fun noMatches() = sendMessage("Nothing found by: $trackURL.").queue()
      override fun loadFailed(e: FriendlyException) = sendMessage("Could not play: ${e.message}").queue()
    })
  }

  private fun play(member: Member, track: AudioTrack) = with(member) {
    connectToVoiceChannel(this, guild.audioManager)
    guild.musicManager.scheduler.queue(track)
  }

  fun pause(channel: TextChannel) = channel.guild.musicManager.player.isPaused.let { with(channel) {
    guild.musicManager.player.isPaused = !it
    sendMessage(if (it) "Playback paused." else "Playback resumed.").queue()
  }}

  fun repeatTrack(channel: TextChannel) = channel.guild.musicManager.scheduler.repeat.let { with(channel) {
    guild.musicManager.scheduler.repeat = !it
    sendMessage(if (it) "Track set to repeat." else "Track set to end.").queue()
  }}

  fun stopTrack(channel: TextChannel) = channel.guild.musicManager.let {
    it.player.stopTrack()
    it.scheduler.clearQueue()
  }

  fun reset(channel: TextChannel) = with(channel) {
    synchronized(musicManagers) {
      guild.musicManager.let {
        it.scheduler.clearQueue()
        it.player.destroy()
        guild.audioManager.sendingHandler = null
        musicManagers.remove(guild.idLong)
      }
      guild.audioManager.sendingHandler = guild.musicManager.sendingHandler
      sendMessage("Player reset.").queue()
    }
  }

  fun shuffleTracks(channel: TextChannel) = channel.guild.musicManager.scheduler.shuffle()

  fun skipTrack(channel: TextChannel) = with(channel) {
    guild.musicManager.scheduler.nextTrack()
    sendMessage("Skipped.").queue()
  }

  @Contract("_ -> param1")
  private fun registerSourceManagers(manager: AudioPlayerManager) = manager.apply {
    registerSourceManager(YoutubeAudioSourceManager().apply { configureRequests {
      RequestConfig.copy(it).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
    }})
    registerSourceManager(SoundCloudAudioSourceManager.createDefault())
    registerSourceManager(TwitchStreamAudioSourceManager())
    registerSourceManager(BandcampAudioSourceManager())
    registerSourceManager(VimeoAudioSourceManager())
    registerSourceManager(BeamAudioSourceManager())
    registerSourceManager(LocalAudioSourceManager())
    registerSourceManager(HttpAudioSourceManager())
  }

  init {
    playerManager = registerSourceManagers(DefaultAudioPlayerManager())
    musicManagers = HashMap()
  }
}