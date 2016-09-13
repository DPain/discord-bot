package com.dpain.DiscordBot.command;

import java.io.IOException;
import java.net.URLDecoder;

import com.dpain.DiscordBot.command.wiki.WikiFinder;
import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class WikipediaCommand extends Command {
	
	public WikipediaCommand() {
		super("WikipediaCommand", Group.USER);
		super.helpString = "**Wikipedia Command Usage:** \n-wiki *\"searchParam\"* : Searches the a topic in Wikipedia.\n";
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
		                
		                if (message.equals("-wiki")) {
		            		castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
		            	} else if (message.startsWith("-wiki ")) {
		            		String searchParam = message.substring(6);
		    				try {
								castedEvent.getChannel().sendMessage("**Wiki Search of** ***" + URLDecoder.decode(searchParam,"UTF-8") + "*** **: **\n" + WikiFinder.search(searchParam));
							} catch (IOException e) {
								castedEvent.getChannel().sendMessage("There were no Wiki results for: " + searchParam);
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
