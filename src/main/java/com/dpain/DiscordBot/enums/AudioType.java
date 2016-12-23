package com.dpain.DiscordBot.enums;

public enum AudioType {
	// Enums of audio types
	FILE("file"),
	URL("url");
	
	private String key;
	AudioType(String key) {
		this.key = key;
	}
	
	public String getKey() {
        return key;
    }
}
