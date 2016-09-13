package com.dpain.DiscordBot.command.audioplayer;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;


public class PlaylistRunner implements Runnable {
	private boolean isRunning = false;
	
	private AudioPlayer audioPlayer;
	
	public PlaylistRunner(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		
		isRunning = true;
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "PlaylistRunner is initialized!"));
	}
	
	@Override
	public void run() {
		while(isRunning) {
			
			/**
	    	 * @TODO Verify that the playlist feature works all the time.
	    	 */
			System.out.println(audioPlayer.getCurrentPlayer().isStopped() && !audioPlayer.getCurrentPlayer().isPaused());
			if(audioPlayer.getCurrentPlayer().isStopped() && !audioPlayer.getCurrentPlayer().isPaused()) {
				if(!audioPlayer.getPlaylist().isEmpty()) {
					audioPlayer.getPlaylist().remove();
					
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Moving to the next track!"));
					audioPlayer.playFirstAudio();
				} else {
					isRunning = false;
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Removing PlaylistRunner because playlist is empty!"));
					audioPlayer.clearPlaylistThread();
					return;
				}
			}
			
		}
	}

	public boolean isRunning() {
		return isRunning;
	}
}
