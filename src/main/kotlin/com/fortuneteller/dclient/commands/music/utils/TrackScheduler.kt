package com.fortuneteller.dclient.commands.music.utils

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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
class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {
  private val queue: Queue<AudioTrack>
  var repeat = false

  val trackList: List<String>
    get() {
      return when (queue.isEmpty()) {
        true -> {
          when (player.playingTrack != null) {
            true -> listOf("`Now playing: ${player.playingTrack.info.title}`")
            false -> listOf("`No tracks left in the queue.`")
          }
        }
        false -> ArrayList<String>().apply {
          add("`Now playing: ${player.playingTrack.info.title}`")
          queue.toList().let {
            it.forEach { t -> add("`${it.indexOf(t) + 1} -> ${t.info.title} -> " +
              "${String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(t.duration))}:" +
              "${String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(t.duration) 
                % TimeUnit.MINUTES.toSeconds(1))}`")
            }
          }
        }
      }
    }

  fun queue(track: AudioTrack) {
    if (player.playingTrack != null) queue.offer(track)
    else player.playTrack(track)
  }

  fun clearQueue() = queue.clear()
  fun nextTrack() = player.playTrack(queue.poll())
  fun shuffle() = (queue as MutableList<*>).shuffle()

  override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, reason: AudioTrackEndReason) = reason.let {
    if (it.mayStartNext) {
      if (repeat) player.playTrack(track.makeClone())
      else nextTrack()
    }
  }

  init {
    queue = LinkedList()
  }
}