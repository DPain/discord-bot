package com.dpain.DiscordBot.enums;

public enum G2gServer {
  /* @formatter:off */
  // Enums of audio types
  ARIA("29360"),
  KADUM("29358"),
  NUI("29356");
  /* @formatter:on */

  private final String id;

  G2gServer(String id) {
    this.id = id;
  }

  public String getID() {
    return id;
  }
}
