package com.dpain.DiscordBot.command.audioplayer;

import com.dpain.DiscordBot.enums.AudioType;

public class Track {
	private AudioType audioType;
	private String name;
	private String directory;
	
	public Track(AudioType audioType, String name, String directory) {
		this.audioType = audioType;
		this.name = name;
		this.directory = directory;
	}
	
	public AudioType getAudioType() {
		return audioType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDirectory() {
		return directory;
	}
}
