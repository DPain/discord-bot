package com.dpain.DiscordBot.command;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class OwnerCommand extends Command {

	public OwnerCommand() {
		super("OwnerCommand", Group.OWNER);
		
		super.helpString = "**Owner Command Usage:** \n"
				+ "-username *\"name\"* : Changes the username of the bot.\n"
				+ "-update : Manually updates the userdata file.\n"
				+ "-group *\"userId\" \"group\"* : Changes the user's group.\n"
				+ "-rebuild : Resets the userdata file to default.\n"
				+ "-exit : Shutsdown the bot.\n";
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
		                if (message.startsWith("-username ")) {
		            		String param = message.substring(10);
		            		castedEvent.getJDA().getAccountManager().setUsername(param);
		            		castedEvent.getJDA().getAccountManager().update();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param);
		            		
		            	} else if(message.startsWith("-group ")) {
		                	/**
		                	 * @TODO Implement user change group feature. Also fix userdata.yml constant read issue.
		                	 */
		                } else if(message.equals("-rebuild")) {
		                	UserManager.load().rebuild(castedEvent.getGuild());
		                } else if(message.equals("-update")) {
		                	UserManager.load().update(castedEvent.getGuild());
		                } else if(message.equals("-reload")) {
		                	UserManager.load().reload();
		                } else if(message.equals("-exit")) {
		                	UserManager.load().saveConfig();
		                	castedEvent.getJDA().shutdown();
		                	System.exit(0);
		                }
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
