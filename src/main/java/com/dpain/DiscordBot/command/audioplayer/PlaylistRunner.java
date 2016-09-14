package com.dpain.DiscordBot.command.audioplayer;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

public class PlaylistRunner implements Runnable {
	private AudioPlayerManager audioPlayerManager;
	
	public PlaylistRunner(AudioPlayerManager audioPlayerManager) {
		this.audioPlayerManager = audioPlayerManager;
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "PlaylistRunner is initialized!"));
	}
	
	@Override
	public void run() {
		while(true) {
			if(audioPlayerManager.getPlaylist().isEmpty()) {
				System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Removing PlaylistRunner because playlist is empty!"));
				audioPlayerManager.clearPlaylistThread();
				return;
			}
			
			/**
			 * @TODO Fix this issue where a delay must be given
			 */
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(audioPlayerManager.getPlayer().isStopped() && !audioPlayerManager.getPlayer().isPaused()) {
				/**
		    	 * @TODO Verify that the playlist feature works all the time.
		    	 */
				audioPlayerManager.getPlaylist().remove();
				
				//Playlist is empty.
				if(audioPlayerManager.getPlaylist().isEmpty()) {
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Removing PlaylistRunner because playlist is empty!"));
					audioPlayerManager.clearPlaylistThread();
					return;
				}
				
				System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Moving to the next track!"));
				audioPlayerManager.playFirstAudio();
			}
		}
	}

}
