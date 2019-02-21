package com.dpain.DiscordBot.plugin.audioplayer;

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
   * Track scheduler for the player.
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
    queue = new LinkedBlockingQueue<>();
    listener = new TrackListener(this);
    player.addListener(listener);
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioPlayerSendHandler getSendHandler() {
    return new AudioPlayerSendHandler(player);
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
