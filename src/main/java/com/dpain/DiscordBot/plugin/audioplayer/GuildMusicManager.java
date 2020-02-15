package com.dpain.DiscordBot.plugin.audioplayer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class GuildMusicManager {
  /**
   * Audio player for the guild.
   */
  private final AudioPlayer player;

  /**
   * Custom -clearAudioEventAdapter for the player.
   */
  private final TrackListener listener;

  /**
   * Queue for the audio player.
   */
  private final BlockingQueue<AudioTrack> queue;

  /**
   * Creates a player and a track scheduler.
   * 
   * @param manager Audio player manager to use for creating the player.
   */
  public GuildMusicManager(AudioPlayerManager manager) {
    player = manager.createPlayer();
    queue = new LinkedBlockingQueue<AudioTrack>();
    listener = new TrackListener(this);
    player.addListener(listener);
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioPlayerSendHandler getSendHandler() {
    return new AudioPlayerSendHandler(player);
  }

  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public void nextTrack(int num) {
    if (num > 0) { 
      if (num > 1) {
        List<AudioTrack> temp = new LinkedList<AudioTrack>();
        queue.drainTo(temp, num - 1);
        temp.clear();
      }
      
      player.startTrack(queue.poll(), false);
    }
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param AudioTrack The track to play or add to queue.
   */
  public void queue(AudioTrack track) {
    if (!player.startTrack(track, true)) {
      queue.offer(track);
    }
  }

  /**
   * Clears the queue.
   */
  public void clear() {
    queue.clear();
  }

  /**
   * Returns whether the queue is empty.
   * 
   * @return boolean
   */
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public AudioPlayer getPlayer() {
    return player;
  }

  public TrackListener getTrackListener() {
    return listener;
  }

  public BlockingQueue<AudioTrack> getQueue() {
    return queue;
  }
}
