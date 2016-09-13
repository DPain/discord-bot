package com.dpain.DiscordBot.exception;

public class PlaylistNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6858714855884776752L;
	private String playlistName;
	
	public PlaylistNotFoundException() {
		super("Could not load the playlist!");
		playlistName = "";
	}
	
	public PlaylistNotFoundException(String playlistName) {
		super("Could not load the playlist: " + playlistName);
		this.playlistName = playlistName;
	}

	public String getAudioName() {
		return playlistName;
	}
}
