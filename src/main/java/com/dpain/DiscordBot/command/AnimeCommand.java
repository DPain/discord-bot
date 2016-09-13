package com.dpain.DiscordBot.command;

import java.io.IOException;
import java.util.LinkedList;

import com.dpain.DiscordBot.command.anime.AnimeTorrentFinder;
import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class AnimeCommand extends Command {
	private AnimeTorrentFinder animeTorrentFinder;
	
	public AnimeCommand() {
		super("AnimeCommand", Group.TRUSTED_USER);
		animeTorrentFinder = new AnimeTorrentFinder();
		
		super.helpString = "**Anime Command Usage:** \n-anime search *\"name\"* : Gets a list of torrrent from Tokyo toshokan.\n-anime today/week : Gets the anime schedule\n";
		EssentialsCommand.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessCommand(castedEvent.getAuthor()) && !castedEvent.getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
					
					if(message.startsWith("-")) {
		                
		                if(message.equals("-anime")) {
		                	//Incorrect usage of anime
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		                	
		                } else if(message.startsWith("-anime ")) {
		                	String param = message.substring(7);
		                	if(param.toLowerCase().startsWith("search ")) {
		                		String searchParam = param.substring(7);
		                		try {
		                			LinkedList<String> torrentInfo = animeTorrentFinder.searchTorrent(searchParam);
		                			for(String msg : torrentInfo) {
		                				castedEvent.getChannel().sendMessage(msg);
		                			}
								} catch (IOException e) {
									castedEvent.getChannel().sendMessage("There were no torrent results for: " + searchParam);
								}
		                	} else if(param.equals("today")) {
		                		animeTorrentFinder.getCurrentSchedule();
		                		castedEvent.getChannel().sendMessage("WIP");
		                		
		                	} else if(param.equals("week")) {
		                		animeTorrentFinder.getFullSchedule();
		                		castedEvent.getChannel().sendMessage("WIP");
		                		
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
