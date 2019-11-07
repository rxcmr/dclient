package dcl.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author rxcmr
 */
public class Loader {
   private final AudioPlayerManager playerManager;
   private final Map<Long, MusicManager> musicManagers;

   public Loader() {
      playerManager = registerSourceManagers(new DefaultAudioPlayerManager());
      musicManagers = new HashMap<>();
   }

   public void loadAndPlay(final TextChannel channel, final String trackUrl) {
      MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
      playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
         @Override
         public void trackLoaded(AudioTrack track) {
            channel.sendMessage(String.format("Adding to queue: *%s*", track.getInfo().title)).queue();
            play(channel.getGuild(), musicManager, track);
         }

         @Override
         public void playlistLoaded(AudioPlaylist playlist) {
            AudioTrack firstTrack = playlist.getSelectedTrack();
            if (firstTrack == null) firstTrack = playlist.getTracks().get(0);
            channel.sendMessage(
               String.format("Adding to queue: *%s* *(first track of playlist %s)*",
                  firstTrack.getInfo().title,
                  playlist.getName())
            ).queue();
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

   private void play(Guild guild, MusicManager musicManager, AudioTrack track) {
      connectToFirstVoiceChannel(guild.getAudioManager());
      musicManager.scheduler.queue(track);
   }

   public void skipTrack(TextChannel channel) {
      MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
      musicManager.scheduler.nextTrack();
      channel.sendMessage("Skipped.").queue();
   }

   private static void connectToFirstVoiceChannel(AudioManager audioManager) {
      if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
         for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
            audioManager.openAudioConnection(voiceChannel);
            break;
         }
      }
   }

   private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
      long guildId = Long.parseLong(guild.getId());
      MusicManager musicManager = musicManagers.get(guildId);

      if (musicManager == null) {
         musicManager = new MusicManager(playerManager);
         musicManagers.put(guildId, musicManager);
      }
      guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
      return musicManager;
   }

   public AudioPlayerManager registerSourceManagers(AudioPlayerManager manager) {
      YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
      youtubeAudioSourceManager.configureRequests(
         config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
      );
      manager.registerSourceManager(youtubeAudioSourceManager);
      manager.registerSourceManager(new SoundCloudAudioSourceManager());
      manager.registerSourceManager(new TwitchStreamAudioSourceManager());
      manager.registerSourceManager(new BandcampAudioSourceManager());
      manager.registerSourceManager(new VimeoAudioSourceManager());
      manager.registerSourceManager(new BeamAudioSourceManager());
      manager.registerSourceManager(new LocalAudioSourceManager());
      manager.registerSourceManager(new HttpAudioSourceManager());
      return manager;
   }
}
