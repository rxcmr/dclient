package dcl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

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
public class AudioHandler implements AudioSendHandler {
   private final AudioPlayer player;
   private final ByteBuffer buffer;
   private final MutableAudioFrame frame;

   public AudioHandler(AudioPlayer player) {
      this.player = player;
      this.buffer = ByteBuffer.allocate(1024);
      this.frame = new MutableAudioFrame();
      this.frame.setBuffer(buffer);
   }

   @Override
   public boolean canProvide() { return player.provide(frame); }

   @Nullable
   @Override
   public ByteBuffer provide20MsAudio() {
      buffer.flip();
      return buffer;
   }

   @Override
   public boolean isOpus() { return true; }
}
