package dcl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author rxcmr
 */
public class TrackScheduler extends AudioEventAdapter {
   private final AudioPlayer player;
   private final BlockingQueue<AudioTrack> queue;

   @Contract(pure = true)
   public TrackScheduler(AudioPlayer player) {
      this.player = player;
      this.queue = new LinkedBlockingQueue<>();
   }

   public void queue(AudioTrack track) {
      if (!player.startTrack(track, true)) queue.offer(track);
   }

   public void nextTrack() { player.startTrack(queue.poll(), false); }

   @Override
   public void onTrackEnd(AudioPlayer player, AudioTrack track, @NotNull AudioTrackEndReason reason) {
      if (reason.mayStartNext) nextTrack();
   }
}
