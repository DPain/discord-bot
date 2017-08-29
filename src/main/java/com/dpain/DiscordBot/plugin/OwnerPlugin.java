package com.dpain.DiscordBot.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.system.MemberManager;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OwnerPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(OwnerPlugin.class.getName());

	public OwnerPlugin() {
		super("OwnerPlugin", Group.OWNER);
		
		super.helpString = "**Owner Plugin Usage:** \n"
				+ "-username *\"name\"* : Changes the username of the bot.\n"
				+ "-update : Manually updates the userdata file.\n"
				+ "-group *\"userId\" \"group\"* : Changes the user's group.\n"
				+ "-rebuild : Resets the userdata file to default.\n"
				+ "-exit : Shutsdown the bot.\n";
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
		                if (message.startsWith("-username ")) {
		            		String param = message.substring(10);
		            		castedEvent.getJDA().getSelfUser().getManager().setName(param);
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param);
		            		
		            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		            	} else if(message.startsWith("-group ")) {
		                	/**
		                	 * @TODO Implement user change group feature. Also fix userdata.yml constant read issue.
		                	 */
		            		
		            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		            	} else if(message.equals("-guild info")) {
		            		castedEvent.getChannel().sendMessage("**Guild Info:**"
		            				+ "\nName: " + castedEvent.getGuild().getName()
		            				+ "\nID: " + castedEvent.getGuild().getId()
		            				+ "\nOwner Nickname: " + castedEvent.getGuild().getOwner().getNickname()
		            				+ "\nOwner ID: " + castedEvent.getGuild().getOwner().getUser().getId()).queue();
		                	
		            		
		            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                } else if(message.equals("-rebuild")) {
		                	MemberManager.load().rebuild(castedEvent.getGuild());
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                } else if(message.equals("-update")) {
		                	MemberManager.load().update(castedEvent.getGuild());
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                } else if(message.equals("-reload")) {
		                	MemberManager.load().reload();
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                } else if(message.equals("-exit")) {
		                	MemberManager.load().saveConfig();
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
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
