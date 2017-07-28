package com.dpain.DiscordBot.plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.AudioNotFoundException;
import com.dpain.DiscordBot.exception.ChannelNotFoundException;
import com.dpain.DiscordBot.exception.NoInstanceException;
import com.dpain.DiscordBot.plugin.audioplayer.GuildMusicManager;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class AudioPlayerPlugin extends Plugin {
	private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
	
	public AudioPlayerPlugin() {
		super("AudioPlayerPlugin", Group.TRUSTED_USER);
		
		super.helpString = "**Audio Player Plugin Usage:** \n"
				+ "-join *\"channelName\"* : Joins a voice channel.\n"
				+ "-leave : Leaves a voice channel.\n"
				+ "-play *\"fileName\"* : Plays an audio file the bot has.\n"
				+ "-playlist *\"name\"* : Plays a playlist the bot has.\n"
				+ "-playurl *\"url\"* : Plays an audio from a url.\n"
				+ "-volume : Displays the current volume.\n"
				+ "-volume *\"integer\"* : Sets the volume of the audio player (0-100).\n"
				+ "-resume : Resumes the audio.\n"
				+ "-pause : Pauses the audio.\n"
				+ "-skip : Skips the audio.\n";
		EssentialsPlugin.appendHelpString(super.helpString);
		
		AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessPlugin(castedEvent.getMember()) && !castedEvent.getAuthor().getId().equals(castedEvent.getJDA().getSelfUser().getId())) {
					
					if(message.startsWith("-")) {
		                
						//Start an audio connection with a VoiceChannel
		                if (message.startsWith("-join ")) {
		                    //Separates the name of the channel so that we can search for it
		                    String chanName = message.substring(6);
		                    
		                    //Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
		                    VoiceChannel channel = castedEvent.getGuild().getVoiceChannels().stream().filter(
		                            vChan -> vChan.getName().equalsIgnoreCase(chanName)).findFirst().orElse(null);  //If there isn't a matching name, return null.
		                    if (channel == null) {
		                    	throw new ChannelNotFoundException();
		                    } else {
		                    	castedEvent.getGuild().getAudioManager().openAudioConnection(channel);
		                    }
		                    
		                } else if (message.equals("-leave")) {
		                	// Disconnect the audio connection with the VoiceChannel.
		                	castedEvent.getGuild().getAudioManager().closeAudioConnection();
		                } else if (message.equals("-play")) {
		                	// Incorrect usage of play
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		                } else if (message.startsWith("-play ")) {
		                	// Plays a local audio file.
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -play"));
		                	
		                	String trackName = message.substring(6);
		                	
		                	// Implement
		                } else if (message.startsWith("-playlist ")) {
		                	// Plays a playlist
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -playlist"));
		                	
		                	String playlistName = message.substring(10);
		                	// Implement
		                } else if (message.startsWith("-playurl ")) {
		                	//Plays audio with the URLPlayer
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -playurl"));
		                	
		                	String urlString = message.substring(9);
		                    loadAndPlay(castedEvent.getChannel(), urlString);
		                } else if(message.equals("-volume")) {
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -volume"));
		                	
		                	// Implement
		                	//castedEvent.getChannel().sendMessage("**Current volume:** *" + audioPlayer.getVolume() + "*");
		        		} else if(message.startsWith("-volume ")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -volume i"));
		        			
		                	String input = message.substring(8);
		                	try {
		                		float temp = Float.parseFloat(input) / 100;
		                		// Implement
		                		//castedEvent.getChannel().sendMessage("Setting the volume to " + audioPlayer.getVolume());
		                	} catch(NumberFormatException e) {
		                		castedEvent.getChannel().sendMessage("You must input an int value between 0-100. (inclusive)");
		                	}
		        		} else if(message.equals("-resume")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -resume"));
		        		} else if(message.equals("-pause")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -pause"));
		        		} else if(message.equals("-skip")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -skip"));
		        			
		        			skipTrack(castedEvent.getChannel());
		        		}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
	    long guildId = Long.parseLong(guild.getId());
	    GuildMusicManager musicManager = musicManagers.get(guildId);

	    if (musicManager == null) {
	      musicManager = new GuildMusicManager(playerManager);
	      musicManagers.put(guildId, musicManager);
	    }

	    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

	    return musicManager;
	}
	
	private void loadAndPlay(final TextChannel channel, final String trackUrl) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

	    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
	      @Override
	      public void trackLoaded(AudioTrack track) {
	        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
	        
	        play(channel.getGuild(), musicManager, track);
	      }

	      @Override
	      public void playlistLoaded(AudioPlaylist playlist) {
	        AudioTrack firstTrack = playlist.getSelectedTrack();

	        if (firstTrack == null) {
	          firstTrack = playlist.getTracks().get(0);
	        }

	        channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

	        play(channel.getGuild(), musicManager, firstTrack);
	      }

	      @Override
	      public void noMatches() {
	        channel.sendMessage("Nothing found by " + trackUrl).queue();
	      }

	      @Override
	      public void loadFailed(FriendlyException exception) {
	        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
	      }
	    });
	  }

	  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
	    connectToFirstVoiceChannel(guild.getAudioManager());

	    musicManager.scheduler.queue(track);
	  }

	  private void skipTrack(TextChannel channel) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    musicManager.scheduler.nextTrack();

	    channel.sendMessage("Skipped to next track.").queue();
	  }

	  private static void connectToFirstVoiceChannel(AudioManager audioManager) {
	    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
	      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
	        audioManager.openAudioConnection(voiceChannel);
	        break;
	      }
	    }
	  }
}
