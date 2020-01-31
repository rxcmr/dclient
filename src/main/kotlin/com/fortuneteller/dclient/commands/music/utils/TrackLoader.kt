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

    private fun connectToVoiceChannel(member: Member, audioManager: AudioManager) {
      when {
        !audioManager.isConnected && !audioManager.isAttemptingToConnect -> audioManager.guild
          .voiceChannels.stream().filter { vc -> vc.members.stream().anyMatch { m -> m.id == member.id } }.findFirst()
          .ifPresent { channel -> audioManager.openAudioConnection(channel) }
        audioManager.guild.voiceChannels.stream().noneMatch { vc ->
          vc.members.stream()
            .anyMatch { m -> m.id == member.id }
        } -> throw CommandException(ExMessage.M_NOT_JOINED)
      }
    }
  }

  fun loadAndPlay(channel: TextChannel, member: Member, trackURL: String): Unit = with(channel) {
    val musicManager = getGuildMusicManager(guild)
    playerManager.loadItemOrdered(musicManager, trackURL, object : AudioLoadResultHandler {
      override fun trackLoaded(track: AudioTrack) {
        sendMessage("Adding to queue: **${track.info.title}**").queue()
        play(guild, member, musicManager, track)
      }

      override fun playlistLoaded(playlist: AudioPlaylist) {
        var firstTrack = playlist.selectedTrack
        if (firstTrack == null) firstTrack = playlist.tracks[0]
        sendMessage(
          "Adding to queue: **${firstTrack?.info?.title}** *(first track of playlist ${playlist.name})*").queue()
        play(guild, member, musicManager, firstTrack)
      }

      override fun noMatches() = sendMessage("Nothing found by: $trackURL.").queue()

      override fun loadFailed(exception: FriendlyException) = sendMessage(
        "Could not play: ${exception.message}").queue()
    })
  }

  fun displayQueue(channel: TextChannel): String {
    val musicManager = getGuildMusicManager(channel.guild)
    return musicManager.scheduler.trackList.joinToString("\n")
  }

  private fun play(guild: Guild, member: Member, musicManager: GuildMusicManager, track: AudioTrack) {
    connectToVoiceChannel(member, guild.audioManager)
    musicManager.scheduler.queue(track)
  }

  fun pause(guild: Guild, paused: Boolean) {
    getGuildMusicManager(guild).player.isPaused = paused
  }

  fun repeatTrack(channel: TextChannel) {
    getGuildMusicManager(channel.guild).scheduler.repeat = !getGuildMusicManager(channel.guild).scheduler.repeat
  }

  fun stopTrack(channel: TextChannel) = getGuildMusicManager(channel.guild).player.stopTrack()

  fun shuffleTracks(channel: TextChannel) = getGuildMusicManager(channel.guild).scheduler.shuffle()

  fun skipTrack(channel: TextChannel) {
    getGuildMusicManager(channel.guild).scheduler.nextTrack()
    channel.sendMessage("Skipped.").queue()
  }

  fun getGuildMusicManager(guild: Guild): GuildMusicManager {
    val musicManager = musicManagers.computeIfAbsent(guild.id.toLong()) { GuildMusicManager(playerManager) }
    guild.audioManager.sendingHandler = musicManager.sendHandler
    return musicManager
  }

  @Contract("_ -> param1")
  private fun registerSourceManagers(manager: AudioPlayerManager) = manager.apply {
    registerSourceManager(YoutubeAudioSourceManager().apply {
      configureRequests { cfg -> RequestConfig.copy(cfg).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build() }
    })
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