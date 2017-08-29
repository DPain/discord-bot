package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.wiki.WikiFinder;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class WikipediaPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(WikipediaPlugin.class.getName());
	
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
		        
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) || canAccessPlugin(castedEvent.getMember())) {
					if(message.startsWith("-")) {
		                
		                if (message.equals("-wiki")) {
		            		castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();
		            	} else if (message.startsWith("-wiki ")) {
		            		String searchParam = message.substring(6);
		    				try {
								castedEvent.getChannel().sendMessage(String.format("**Wiki Search of** ***%s*** **: **\n%s",
										URLDecoder.decode(searchParam, "UTF-8"),
										WikiFinder.search(searchParam))).queue();
							} catch (IOException e) {
								castedEvent.getChannel().sendMessage("There were no Wiki results for: " + searchParam).queue();
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
