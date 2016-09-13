package com.dpain.DiscordBot.command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.dpain.DiscordBot.command.anime.AnimeTorrentFinder;
import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class ProfanityGuardCommand extends Command {
	private boolean activated = false;
	private HashMap<String, File> emoteMap;
	
	/**
	 * @TODO In future, implement strike out system and save into a file
	 */
	
	public ProfanityGuardCommand() {
		super("ProfanityGuardCommand", Group.MODERATOR);
		
		super.helpString = "**Profanity Guard Command Usage:** \n" +
				"-profguard *\"enable/disable\"* : Enables or disables Profanity Guard\n" +
				"-profguard *\"offenders\"* : PMs the list of offenders to the one who issued the command\n";
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
		                
		                if(message.equals("-profguard")) {
		                	//Incorrect usage of profguard
		                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		                	
		                } else if(message.startsWith("-profguard ")) {
		                	String param = message.substring(11);
		                	if(param.toLowerCase().equals("enable")) {
		                		activated = true;
		                		castedEvent.getChannel().sendMessage("*Profanity Guard is ENABLED!*");
		                	} else if(param.equals("disable")) {
		                		activated = false;
		                		castedEvent.getChannel().sendMessage("*Profanity Guard is DISABLED!*");
		                	} else if(param.equals("offenders")) {
		                		
		                		castedEvent.getAuthor().getPrivateChannel().sendMessage("WIP");
		                	} else {
		                		//Incorrect usage of profguard
			                	castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
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
