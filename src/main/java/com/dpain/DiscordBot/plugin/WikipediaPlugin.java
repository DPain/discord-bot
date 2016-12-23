package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.net.URLDecoder;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.plugin.wiki.WikiFinder;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class WikipediaPlugin extends Plugin {
	
	public WikipediaPlugin() {
		super("WikipediaPlugin", Group.USER);
		super.helpString = "**Wikipedia Plugin Usage:** \n-wiki *\"searchParam\"* : Searches the a topic in Wikipedia.\n";
		EssentialsPlugin.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) || canAccessPlugin(castedEvent.getAuthor())) {
					
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
