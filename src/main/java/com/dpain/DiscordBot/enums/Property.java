package com.dpain.DiscordBot.enums;

public enum Property {
  /* @formatter:off */
  // Enums of each properties for the bot.
  BOT_TOKEN("bot-token"),
  OWNER_USER_ID("owner-user-id"),
  GUILD_ID("guild-id"),
  WEATHER_API_KEY("weather-api-key"),
  USE_GAME_ROLE("use-game-role"),
  USE_TWITCH_ALERTER("use-twitch-alerter"),
  G2G_ALERT_INTERVAL("g2g-alert-interval"),
  TORRENT_ENTRY_LIMIT("torrent-entry-limit");
  /* @formatter:on */

  private final String key;

  Property(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
