package com.dpain.DiscordBot.plugin.audioplayer;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

public class PlaylistRunner implements Runnable {
	private AudioPlayerManager audioPlayerManager;
	private boolean threadShouldRun;
	
	public PlaylistRunner(AudioPlayerManager audioPlayerManager) {
		this.audioPlayerManager = audioPlayerManager;
		threadShouldRun = true;
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "PlaylistRunner is initialized!"));
	}
	
	@Override
	public void run() {
		
		while(threadShouldRun) {
			if(!audioPlayerManager.getPlaylist().isEmpty() && audioPlayerManager.getPlayer() != null) {
				/**
				 * @TODO Fix this issue where a delay must be given
				 */
				try {
					//Thread.sleep(10);
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(audioPlayerManager.getPlayer().isStopped() && !audioPlayerManager.getPlayer().isPaused()) {
					/**
			    	 * @TODO Verify that the playlist feature works all the time.
			    	 */
					audioPlayerManager.getPlaylist().remove();
					
					/**
					//Playlist is empty.
					if(audioPlayerManager.getPlaylist().isEmpty()) {
						System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Removing PlaylistRunner because playlist is empty!"));
						audioPlayerManager.clearPlaylistThread();
						return;
					}
					*/
					
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistRunner", "Moving to the next track!"));
					audioPlayerManager.playFirstTrack();
				}
			}
		}
	}
	
	public void destroy() {
		threadShouldRun = false;
	}

}
