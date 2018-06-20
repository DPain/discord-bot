package com.dpain.DiscordBot.plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.AudioNotFoundException;
import com.dpain.DiscordBot.exception.ChannelNotFoundException;
import com.dpain.DiscordBot.exception.NoInstanceException;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.audioplayer.GuildMusicManager;
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
	private final static Logger logger = Logger.getLogger(AudioPlayerPlugin.class.getName());
	
	private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
	
	public AudioPlayerPlugin() {
		this(null);
	}
	
	public AudioPlayerPlugin(TextChannel loggingChannel) {
		super("AudioPlayerPlugin", Group.TRUSTED_USER, loggingChannel);
		
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
		                    	String temp = LogHelper.elog(castedEvent, String.format("Channel does not exist Command: %s", message));
			                	logger.log(Level.INFO, temp);
			                	if(this.loggingChannel != null) {
			                		this.loggingChannel.sendMessage(temp);
			                	}
		                    	throw new ChannelNotFoundException();
		                    } else {
		                    	castedEvent.getGuild().getAudioManager().openAudioConnection(channel);
		                    	String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
			                	logger.log(Level.INFO, temp);
			                	if(this.loggingChannel != null) {
			                		this.loggingChannel.sendMessage(temp);
			                	}
		                    }
		                } else if (message.equals("-leave")) {
		                	// Disconnect the audio connection with the VoiceChannel.
		                	castedEvent.getGuild().getAudioManager().closeAudioConnection();
		                	String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		                } else if (message.equals("-play")) {
		                	// Incorrect usage of play
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		                	String temp = LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message));
		                	logger.log(Level.WARNING, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		                } else if (message.startsWith("-play ")) {
		                	// Plays a local audio file.
		                	String trackName = message.substring(6);
		                	// Implement
		                	
		                	String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		                } else if (message.startsWith("-playlist ")) {
		                	// Plays a playlist
		                	
		                	String playlistName = message.substring(10);
		                	// Implement

		                	String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		                } else if (message.startsWith("-playurl ")) {
		                	//Plays audio with the URLPlayer
		                	
		                	String urlString = message.substring(9);
		                    loadAndPlay(castedEvent.getChannel(), urlString);

		                    String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		                } else if(message.equals("-volume")) {
		                	castedEvent.getChannel().sendMessage(String.format("**Current volume:** *%d*",
		                			getVolume(castedEvent.getChannel()))).queue();

		                	String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		        		} else if(message.startsWith("-volume ")) {
		                	String input = message.substring(8);
		                	try {
		                		int temp = Integer.parseInt(input);
		                		setVolume(castedEvent.getChannel(), temp);
		                		
		                		castedEvent.getChannel().sendMessage(String.format("Setting the volume to %d",
		                				getVolume(castedEvent.getChannel()))).queue();

		                		String temp0 = LogHelper.elog(castedEvent, String.format("Command: %s", message));
			                	logger.log(Level.INFO, temp0);
			                	if(this.loggingChannel != null) {
			                		this.loggingChannel.sendMessage(temp0);
			                	}
		                	} catch(NumberFormatException e) {
		                		castedEvent.getChannel().sendMessage("You must input an int value between 0-100. (inclusive)").queue();
		                		
		                		String temp1 = LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message));
			                	logger.log(Level.WARNING, temp1);
			                	if(this.loggingChannel != null) {
			                		this.loggingChannel.sendMessage(temp1);
			                	}
		                	}
		        		} else if(message.equals("-resume")) {
		        			System.out.println("");

		        			String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		        		} else if(message.equals("-pause")) {
		        			System.out.println("");

		        			String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
		        		} else if(message.equals("-skip")) {
		        			skipTrack(castedEvent.getChannel());

		        			String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
		                	logger.log(Level.INFO, temp);
		                	if(this.loggingChannel != null) {
		                		this.loggingChannel.sendMessage(temp);
		                	}
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
	
	private void setVolume(final TextChannel channel, int volume) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.player.setVolume(volume);
	}
	
	private int getVolume(final TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		return musicManager.player.getVolume();
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
