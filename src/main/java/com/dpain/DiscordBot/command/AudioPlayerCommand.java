package com.dpain.DiscordBot.command;

import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.dpain.DiscordBot.command.audioplayer.AudioPlayer;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.AudioNotFoundException;
import com.dpain.DiscordBot.exception.ChannelNotFoundException;
import com.dpain.DiscordBot.exception.NoInstanceException;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class AudioPlayerCommand extends Command {
	private AudioPlayer audioPlayer;
	
	public AudioPlayerCommand() {
		super("AudioPlayerCommand", Group.TRUSTED_USER);
		
		super.helpString = "**Audio Player Command Usage:** \n"
				+ "-join *\"channelName\"* : Joins a voice channel.\n"
				+ "-leave : Leaves a voice channel.\n"
				+ "-play *\"fileName\"* : Plays an audio file the bot has.\n"
				+ "-playlist *\"name\"* : Plays a playlist the bot has.\n"
				+ "-playurl *\"url\"* : Plays an audio from a url.\n"
				+ "-volume : Displays the current volume.\n"
				+ "-volume *\"integer\"* : Sets the volume of the audio player (0-100).\n"
				+ "-resume : Resumes the audio.\n"
				+ "-pause : Pauses the audio.\n"
				+ "-remove : Removes the audio.\n";
		EssentialsCommand.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessCommand(castedEvent.getAuthor()) && !castedEvent.getAuthor().getId().equals(castedEvent.getJDA().getSelfInfo().getId())) {
					
					if(message.startsWith("-")) {
		                
						//Start an audio connection with a VoiceChannel
		                if (message.startsWith("-join ")) {
		                    //Separates the name of the channel so that we can search for it
		                    String chanName = message.substring(6);
		                    
		                    audioPlayer = new AudioPlayer(castedEvent.getGuild());
		                    
		                    try {
		                    	audioPlayer.join(chanName);
		                    } catch(ChannelNotFoundException e) {
		                    	castedEvent.getChannel().sendMessage("There isn't a VoiceChannel in this Guild with the name: '" + chanName + "'");
		                    }
		                } else if (message.equals("-leave")) {
		                	//Disconnect the audio connection with the VoiceChannel.
		                	castedEvent.getGuild().getAudioManager().closeAudioConnection();
		                } else if (message.equals("-play")) {
		                	//Incorrect usage of play
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		                } else if (message.startsWith("-play ")) {
		                	//Plays audio with the FilePlayer
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -play"));
		                	
		                	String trackName = message.substring(6);
		                	try {
		                		audioPlayer.play(trackName);
		                	} catch(AudioNotFoundException e) {
		                		castedEvent.getChannel().sendMessage("Could not load the file. Does it exist?\n**File Name:** *" + e.getAudioName() + "*");
		                	} catch(UnsupportedAudioFileException e) {
		                		castedEvent.getChannel().sendMessage("Could not load file. It either isn't an audio file or isn't a recognized audio format.");
		                	}
		                	
		                	
		                } else if (message.startsWith("-playlist ")) {
		                	//Plays a playlist
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -playlist"));
		                	
		                	String playlistName = message.substring(10);
		                	try {
		                		audioPlayer.playPlaylist(playlistName);
		                	} catch(AudioNotFoundException e) {
		                		castedEvent.getChannel().sendMessage("Could not load the playlist. Does it exist?\n**Playlist Name:** *" + e.getAudioName() + "*");
		                	} catch(UnsupportedAudioFileException e) {
		                		castedEvent.getChannel().sendMessage("Could not load playlist. It might contain an audio file that isn't recognizable.");
		                	}
		                	
		                	
		                } else if (message.startsWith("-playurl ")) {
		                	//Plays audio with the URLPlayer
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -playurl"));
		                	
		                	URL audioURL = null;
		                    try {
		                    	audioURL = new URL(message.substring(9));
		        				audioPlayer.playURL(audioURL);
		        			} catch (AudioNotFoundException e) {
		                        castedEvent.getChannel().sendMessage("Could not load the url. Does it exist?\n**URL:** *" + e.getAudioName() + "*");
		        			} catch (UnsupportedAudioFileException e) {
		                        castedEvent.getChannel().sendMessage("The file is not formatted with a recognized audio format.");
		                    }
		                } else if(message.equals("-volume")) {
		                	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -volume"));
		                	
		                	castedEvent.getChannel().sendMessage("**Current volume:** *" + audioPlayer.getVolume() + "*");
		        		} else if(message.startsWith("-volume ")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -volume i"));
		        			
		                	String input = message.substring(8);
		                	try {
		                		float temp = Float.parseFloat(input) / 100;
		                		audioPlayer.setVolume(temp);
		                		castedEvent.getChannel().sendMessage("Setting the volume to " + audioPlayer.getVolume());
		                	} catch(NumberFormatException e) {
		                		castedEvent.getChannel().sendMessage("You must input an int value between 0-100. (inclusive)");
		                	}
		        		} else if(message.equals("-resume")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -resume"));
		        			
		        			try {
		        				audioPlayer.resume();
		        			} catch(NoInstanceException e) {
		        				castedEvent.getChannel().sendMessage("You need to have an audio in the queue before you can preform that command.");
		        			}
		        		} else if(message.equals("-pause")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -pause"));
		        			
		        			try {
		        				audioPlayer.pause();
		        			} catch(NoInstanceException e) {
		        				castedEvent.getChannel().sendMessage("You need to have an audio in the queue before you can preform that command.");
		        			}
		        		} else if(message.equals("-remove")) {
		        			System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "Received a command: -remove"));
		        			
		        			try {
		        				audioPlayer.remove();
		        			} catch(NoInstanceException e) {
		        				castedEvent.getChannel().sendMessage("You need to have an audio in the queue before you can preform that command.");
		        			}
		        		}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
