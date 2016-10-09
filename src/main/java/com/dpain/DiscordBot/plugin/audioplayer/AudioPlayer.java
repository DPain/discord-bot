package com.dpain.DiscordBot.plugin.audioplayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.UnsupportedAudioFileException;

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

public class AudioPlayer {
	private Queue<Track> playlist = null;
	private float volume;
	private Guild guild;
	
	private Thread playlistThread = null;
	
	public AudioPlayer(Guild guild) {
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
    	
        try {
        	FilePlayer instance = new FilePlayer(audioFile);
    		instance.setVolume(volume);
    		playlist.add(new Track(trackName, instance));
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Added a track into the queue: " + audioFile.getName()) + ", Queue size: " + playlist.size());
		} catch (IOException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Could not load the track: " + audioFile.getName()));
			throw new AudioNotFoundException(audioFile.getName());
		} catch (UnsupportedAudioFileException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "The file is not formatted with a recognized audio format."));
            throw new UnsupportedAudioFileException();
        }

        //If the newly added player is the only player in the queue, play the new audio.
        if(playlist.size() <= 1) {
        	if(playlistThread == null) {
        		playlistThread = new Thread(new PlaylistRunner(this));
        		playlistThread.start();
        	}
        	playFirstAudio();
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
	    		try {
        			FilePlayer instance = new FilePlayer(audioFile);
        			instance.setVolume(volume);
        			temp.add(new Track(audioFile.getName(), instance));
	    		} catch (IOException e) {
	    			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Could not load a track: " + audioFile.getName()));
	    		} catch (UnsupportedAudioFileException e) {
	    			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "The file is not formatted with a recognized audio format."));
	            }
    		}
    		
    		playlist.addAll(temp);
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Added " + temp.size() + " tracks into the queue. Queue size: " + playlist.size()));
            
            //If the newly added player is the only player in the queue, play the new audio.
	        if(playlist.size() <= temp.size()) {
	        	if(playlistThread == null) {
	        		playlistThread = new Thread(new PlaylistRunner(this));
	        		playlistThread.start();
	        	}
	        	playFirstAudio();
	    	}
    	}
	}
	
	public void playURL(URL audioURL) throws UnsupportedAudioFileException, AudioNotFoundException{
		try {
			URLPlayer instance = new URLPlayer(guild.getJDA(), audioURL);
			instance.setVolume(volume);
			playlist.add(new Track(audioURL.getFile(), instance));
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Added a URL track into the queue: " + audioURL.getFile() + ", Queue size: " + playlist.size()));
		} catch (IOException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Could not load the url: " + audioURL.toString()));
			throw new AudioNotFoundException(audioURL.toString());
		} catch (UnsupportedAudioFileException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "The file is not formatted with a recognized audio format!"));
            throw new UnsupportedAudioFileException();
        }

		//If the newly added player is the only player in the queue, play the new audio.
        if(playlist.size() <= 1) {
        	if(playlistThread == null) {
        		playlistThread = new Thread(new PlaylistRunner(this));
        		playlistThread.start();
        	}
        	playFirstAudio();
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
		
		if(playlist.peek() != null) {
			playlist.peek().player.setVolume(volume);
    	}
	}
	
	public void playFirstAudio() {
		guild.getAudioManager().setSendingHandler(playlist.peek().player);
		playlist.peek().player.setVolume(volume);
    	playlist.peek().player.play();
    	
    	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Playing a track in the playlist: " + playlist.peek().name + ", Queue size: " + playlist.size()));
	}
	
	public void resume() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "There were no Player instances instantiated!"));
    		throw new NoInstanceException();
    	} else {
    		playlist.peek().player.setVolume(volume);
        	playlist.peek().player.play();
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Resumed the audio player!"));
    	}
	}
	
	public void pause() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "There were no Player instances instantiated!"));
    		throw new NoInstanceException();
    	} else {
    		playlist.peek().player.pause();
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Paused the audio player!"));
    	}
	}
	
	public void remove() throws NoInstanceException {
		if(playlist.isEmpty()) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "There were no Player instances instantiated!"));
    		throw new NoInstanceException();
    	} else {
    		playlist.peek().player.stop();
    		playlist.remove();
    		
    		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("AudioPlayer", "Removed the track from the audio player. Queue size: " + playlist.size()));
    	}
	}
	
	public boolean isOpus() {
		return playlist.peek().player.isOpus();
	}
	
	public boolean isPaused() {
		return playlist.peek().player.isPaused();
	}
	
	public boolean isPlaying() {
		return playlist.peek().player.isPlaying();
	}
	
	public boolean isStarted() {
		return playlist.peek().player.isStarted();
	}
	
	public boolean isStopped() {
		return playlist.peek().player.isStopped();
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public Queue<Track> getPlaylist() {
		return playlist;
	}
	
	public Player getCurrentPlayer() {
		return playlist.peek().player;
	}
	
	public class Track {
		public String name;
		public Player player;
		public Track(String name, Player player) {
			this.name = name;
			this.player = player;
		}
	}
	
	public void clearPlaylistThread() {
		playlistThread = null;
	}
}
