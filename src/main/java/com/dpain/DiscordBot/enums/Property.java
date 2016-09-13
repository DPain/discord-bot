package com.dpain.DiscordBot.enums;

public enum Property {
	// Enums of each properties for the bot
	USERNAME("username"),
	ACCEPT_INVITES("accept-invites"),
	BOT_ID("bot-id"),
	BOT_TOKEN("bot-token"),
	APP_ID("app-id"),
	OWNER_USER_ID("owner-user-id"),
	GUILD_ID("guild-id"),
	WEATHER_API_KEY("weather-api-key");
	
	private String key;
	Property(String key) {
		this.key = key;
	}
	
	public String getKey() {
        return key;
    }
}
