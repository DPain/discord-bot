package com.dpain.DiscordBot.command;

import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class CustomCommand extends Command {

	public CustomCommand() {
		super("CustomCommand", Group.TRUSTED_USER);
		
		super.helpString = "**Custom Command Usage:** \n"
				+ "-custom : Displays all the custom commands created by the users from the server.\n"
				+ "-delcustom *\"userId\"* : Deletes a custom command.\n";
		EssentialsCommand.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessCommand(castedEvent.getAuthor()) && !castedEvent.getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
					
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
