package com.fortuneteller.dclient.commands.music.utils;
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


import com.fortuneteller.dclient.utils.PilotUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class TrackScheduler extends AudioEventAdapter {
  private final AudioPlayer player;
  private final Queue<AudioTrack> queue;

  @Contract(pure = true)
  public TrackScheduler(AudioPlayer player) {
    this.player = player;
    this.queue = new LinkedTransferQueue<>();
  }

  public void queue(AudioTrack track) {
    if (!player.startTrack(track, true) && queue.offer(track)) {
      PilotUtils.info("A track has been queued.");
    }
  }

  public List<String> getTrackList() {
    return queue.stream()
      .map(t -> "`" + t.getPosition() + " - " + t.getInfo().title + "`")
      .collect(Collectors.toCollection(LinkedList::new)).isEmpty()
      ? Collections.singletonList("No tracks left in the queue.")
      : queue.stream()
      .map(t -> "`" + t.getPosition() + " - " + t.getInfo().title + "`")
      .collect(Collectors.toCollection(LinkedList::new));
  }

  public void nextTrack() {
    player.startTrack(queue.poll(), false);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, @NotNull AudioTrackEndReason reason) {
    if (reason.mayStartNext) nextTrack();
  }
}
