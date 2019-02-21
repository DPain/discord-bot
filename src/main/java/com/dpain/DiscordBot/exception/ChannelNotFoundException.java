package com.dpain.DiscordBot.exception;

public class ChannelNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -3238844090541794881L;

  public ChannelNotFoundException() {
    super("There were no such channel!");
  }

}
