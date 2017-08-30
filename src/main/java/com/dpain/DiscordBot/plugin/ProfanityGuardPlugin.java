package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ProfanityGuardPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(ProfanityGuardPlugin.class.getName());
	
	private boolean activated = false;
	private HashMap<String, File> emoteMap;
	
	/**
	 * @TODO In future, implement strike out system and save into a file
	 */
	
	public ProfanityGuardPlugin() {
		super("ProfanityGuardPlugin", Group.MODERATOR);
		
		super.helpString = "**Profanity Guard Plugin Usage:** \n" +
				"-profguard *\"enable/disable\"* : Enables or disables Profanity Guard\n" +
				"-profguard *\"offenders\"* : PMs the list of offenders to the one who issued the command\n";
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
		                if(message.equals("-profguard")) {
		                	//Incorrect usage of profguard
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();
		                	logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
		                } else if(message.startsWith("-profguard ")) {
		                	String param = message.substring(11);
		                	if(param.toLowerCase().equals("enable")) {
		                		activated = true;
		                		castedEvent.getChannel().sendMessage("*Profanity Guard is ENABLED!*");
		                		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                	} else if(param.equals("disable")) {
		                		activated = false;
		                		castedEvent.getChannel().sendMessage("*Profanity Guard is DISABLED!*");
		                		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                	} else if(param.equals("offenders")) {
		                		castedEvent.getAuthor().openPrivateChannel().complete().sendMessage("WIP");
		                		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                	} else {
		                		//Incorrect usage of profguard
			                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
			                	logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
		                	}
		                }
					}
					/*
					if() {
						
					}
					*/
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
