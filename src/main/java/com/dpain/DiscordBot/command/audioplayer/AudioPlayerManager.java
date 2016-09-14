package com.dpain.DiscordBot.command.audioplayer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.dpain.DiscordBot.enums.AudioType;
import com.dpain.DiscordBot.exception.AudioNotFoundException;
import com.dpain.DiscordBot.exception.ChannelNotFoundException;
import com.dpain.DiscordBot.exception.NoInstanceException;
import com.dpain.DiscordBot.exception.PlaylistNotFoundException;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.audio.player.URLPlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceChannel;

public class AudioPlayerManager {
	private Player player;
	private Queue<Track> playlist = null;
	private float volume;
	private Guild guild;
	
	private Thread playlistThread = null;
	
	public AudioPlayerManager(Guild guild) {
		this.playlist = new LinkedList<Track>();
		this.guild = guild;
		this.volume = 1f;
	}
	
	public void join(String chanName) throws ChannelNotFoundException {
		
        //Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
        VoiceChannel channel = guild.getVoiceChannels().stream().filter(
                vChan -> vChan.getName().equalsIgnoreCase(chanName))
                .findFirst().orElse(null);  //If there isn't a matching name, return null.
        if (channel == null) {
        	throw new ChannelNotFoundException();
        }
        guild.getAudioManager().openAudioConnection(channel);
	}
	
	public void leave() {
		guild.getAudioManager().closeAudioConnection();
	}
	
	public void play(String trackName) throws AudioNotFoundException, UnsupportedAudioFileException {
		//Plays audio with the FilePlayer
		
    	File audioFile = new File("./audio/" + trackName + ".mp3");
    	
    	if(audioFile.exists() && audioFile.isFile()) {
    		playlist.add(new Track(AudioType.FILE, audioFile.getName(), audioFile.getPath()));
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Added a track into the queue: " + audioFile.getName()) + ", Queue size: " + playlist.size());

    		//If the newly added player is the only player in the queue, play the new audio.
    		//Starting the thread plays the audio at the start
	        if(playlist.size() <= 1) {
	        	playFirstAudio();
	        	if(playlistThread == null) {
	        		playlistThread = new Thread(new PlaylistRunner(this));
	        		playlistThread.start();
	        	}
	    	}
    	} else {
    		throw new AudioNotFoundException(audioFile.getName());
    	}
	}
	
	public void playPlaylist(String playlistName) throws AudioNotFoundException, UnsupportedAudioFileException, PlaylistNotFoundException, IOException {
		//Plays audio with the FilePlayer
		
    	File audioFolder = new File("./audio/playlist/" + playlistName + "/");
    	
    	if(!audioFolder.isDirectory() || !audioFolder.exists()) {
    		throw new PlaylistNotFoundException(audioFolder.getName());
    	} else {
    		LinkedList<Track> temp = new LinkedList<Track>();
    		for(File audioFile : audioFolder.listFiles()) {
    			
    			if(audioFile.exists() && audioFile.isFile()) {
    				temp.add(new Track(AudioType.FILE, audioFile.getName(), audioFile.getPath()));
    	    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Added a track into the queue: " + audioFile.getName()));
    	    	} else {
    	    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "An audio file was not found: " + audioFile.getName()));
    	    	}
    		}
    		
    		playlist.addAll(temp);
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Added " + temp.size() + " tracks into the queue. Queue size: " + playlist.size()));
            
            //If the newly added player is the only player in the queue, play the new audio.
    		//Starting the thread plays the audio at the start
	        if(playlist.size() <= temp.size()) {
	        	playFirstAudio();
	        	if(playlistThread == null) {
	        		playlistThread = new Thread(new PlaylistRunner(this));
	        		playlistThread.start();
	        	}
	        }
    	}
	}
	
	public void playURL(URL audioURL) throws AudioNotFoundException, UnsupportedAudioFileException {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) audioURL.openConnection();
			con.setRequestMethod("HEAD");
			
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				playlist.add(new Track(AudioType.URL, audioURL.getFile(), audioURL.toString()));
				System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Added a URL track into the queue: " + audioURL.getFile() + ", Queue size: " + playlist.size()));
			} else {
				System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Could not load the url: " + audioURL.toString()));
				throw new AudioNotFoundException(audioURL.toString());
			}
			con.disconnect();
	    }
	    catch (Exception e) {
	    	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Error: " + audioURL.toString()));
			e.printStackTrace();
	    }

		//If the newly added player is the only player in the queue, play the new audio.
        if(playlist.size() <= 1) {
        	playFirstAudio();
        	if(playlistThread == null) {
        		playlistThread = new Thread(new PlaylistRunner(this));
        		playlistThread.start();
        	}
    	}
	}
	
	public int getVolume() {
		return (int) (volume * 100);
	}
	
	public void setVolume(float value) {
		//Limits the volume
		if(value > 1f) {
			value = 1f;
		} else if(value < 0f) {
			value = 0f;
		}
		//Sets the volume
		volume = value;
		
		if(player != null) {
			player.setVolume(volume);
    	}
	}
	
	public void playFirstAudio() {
		if(!playlist.isEmpty()) {
			Player temp = null;
			if(playlist.peek().getAudioType() == AudioType.FILE) {
				File audioFile = new File(playlist.peek().getDirectory());
				try {
					temp = new FilePlayer(audioFile);
				} catch (IOException | UnsupportedAudioFileException e) {
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "The file is not formatted with a recognized audio format."));
				}
			} else {
				try {
					temp = new URLPlayer(guild.getJDA(), new URL(playlist.peek().getDirectory()));
				} catch (IOException | UnsupportedAudioFileException e) {
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "The file is not formatted with a recognized audio format."));
				}
			}
			
			if(temp != null) {
				player = temp;
				guild.getAudioManager().setSendingHandler(player);
				player.setVolume(volume);
		    	player.play();
		    	
		    	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Playing a track in the playlist: " + playlist.peek().getName()));
			}
    	}
    	
	}
	
	public void resume() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "There were no tracks in the playlist!"));
			clearPlaylistThread();
    		throw new NoInstanceException();
    	} else {
    		player.setVolume(volume);
        	player.play();
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Resumed the audio player!"));
    	}
	}
	
	public void pause() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "There were no tracks in the playlist!"));
			clearPlaylistThread();
    		throw new NoInstanceException();
    	} else {
    		player.pause();
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Paused the audio player!"));
    	}
	}
	
	public void remove() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "There were no tracks in the playlist!"));
			clearPlaylistThread();
    		throw new NoInstanceException();
    	} else {
    		player.stop();
    		player = null;
    		playlist.remove();
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayerManager", "Removed the track from the audio player. Queue size: " + playlist.size()));
    		
    		playFirstAudio();
    	}
	}
	
	public boolean isOpus() {
		return player.isOpus();
	}
	
	public boolean isPaused() {
		return player.isPaused();
	}
	
	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	public boolean isStarted() {
		return player.isStarted();
	}
	
	public boolean isStopped() {
		return player.isStopped();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Queue<Track> getPlaylist() {
		return playlist;
	}
	
	public Track getCurrentTrack() {
		return playlist.peek();
	}
	
	public void clearPlaylistThread() {
		playlistThread = null;
	}
}
