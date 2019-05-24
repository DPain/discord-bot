package com.dpain.DiscordBot.enums;

public enum AudioType {
  /* @formatter:off */
  // Enums of audio types
  FILE("file"),
  URL("url");
  /* @formatter:on */

  private final String key;

  AudioType(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
