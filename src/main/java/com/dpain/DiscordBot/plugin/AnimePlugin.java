package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class AnimePlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(AnimePlugin.class.getName());
	
	private AnimeTorrentFinder animeTorrentFinder;
	
	public AnimePlugin() {
		super("AnimePlugin", Group.TRUSTED_USER);
		animeTorrentFinder = new AnimeTorrentFinder();
		
		super.helpString = "**Anime Plugin Usage:** \n"
				+ "-anime search *\"name\"* : Gets a list of torrrent from Tokyo toshokan.\n"
				+ "-anime today/week : Gets the anime schedule\n";
		EssentialsPlugin.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) || canAccessPlugin(castedEvent.getMember())) {
					
					if(message.startsWith("-")) {
		                if(message.equals("-anime")) {
		                	//Incorrect usage of anime plugin.
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();
		                	logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
		                } else if(message.startsWith("-anime ")) {
		                	String param = message.substring(7);
		                	if(param.toLowerCase().startsWith("search ")) {
		                		String searchParam = param.substring(7);
		                		try {
		                			LinkedList<String> torrentInfo = animeTorrentFinder.searchTorrent(searchParam);
		                			for(String msg : torrentInfo) {
		                				castedEvent.getChannel().sendMessage(msg).queue();
		                			}
								} catch (IOException e) {
									castedEvent.getChannel().sendMessage("There were no torrent results for: " + searchParam).queue();
								}
		                	} else if(param.equals("today")) {
		                		animeTorrentFinder.getCurrentSchedule();
		                		castedEvent.getChannel().sendMessage("WIP").queue();
		                	} else if(param.equals("week")) {
		                		animeTorrentFinder.getFullSchedule();
		                		castedEvent.getChannel().sendMessage("WIP").queue();
		                	}
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                }
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
