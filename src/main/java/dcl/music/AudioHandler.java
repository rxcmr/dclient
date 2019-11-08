package dcl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

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
