package com.dpain.DiscordBot.plugin;

import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.listener.UserEventListener;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class CustomCommandPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(CustomCommandPlugin.class.getName());

	public CustomCommandPlugin() {
		super("CustomCommandPlugin", Group.TRUSTED_USER);
		
		super.helpString = "**Custom Plugin Usage:** \n"
				+ "-custom : Displays all the custom commands created by the users from the server.\n"
				+ "-delcustom *\"userId\"* : Deletes a custom command.\n";
		EssentialsPlugin.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContentRaw();
		        
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) || canAccessPlugin(castedEvent.getMember())) {
					
					if(message.startsWith("!")) {
		                /*
		                 * Implement
		                 */
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
