package com.dpain.DiscordBot.plugin.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackListener extends AudioEventAdapter {
  private final GuildMusicManager musicMgr;

  /**
   * @param GuildMusicManager The music manager for the guild
   */
  public TrackListener(GuildMusicManager musicMgr) {
    this.musicMgr = musicMgr;
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param AudioTrack The track to play or add to queue.
   */
  public void queue(AudioTrack track) {
    if (!musicMgr.getPlayer().startTrack(track, true)) {
      musicMgr.getQueue().offer(track);
    }
  }

  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public void nextTrack() {
    musicMgr.getPlayer().startTrack(musicMgr.getQueue().poll(), false);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    }
  }
}
