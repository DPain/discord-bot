package com.dpain.DiscordBot.exception;

public class AudioNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -1220053575230289581L;

  private String audioName;

  public AudioNotFoundException() {
    super("Could not load the tracks!");
    audioName = "";
  }

  public AudioNotFoundException(String audioName) {
    super("Could not load the track: " + audioName);
    this.audioName = audioName;
  }

  public String getAudioName() {
    return audioName;
  }
}
