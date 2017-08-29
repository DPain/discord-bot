package com.dpain.DiscordBot.plugin;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.moderator.Cleaner;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ModeratorPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(ModeratorPlugin.class.getName());

	public ModeratorPlugin() {
		super("ModeratorPlugin", Group.MODERATOR);
		
		super.helpString = "**Moderator Plugin Usage:**\n"
				+ "-nick *\"name\"* : Changes the nickname of the bot.\n"
				+ "-randomnick : Randomly changes the nickname of the bot.\n"
				+ "-clear *\"channelName\"* : Clears all the messages in the text channel\n";
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
		                if (message.startsWith("-nick ")) {
		            		String param = message.substring(6);
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getMember(), param).queue();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param).queue();
		            		
		            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		            	} else if (message.equals("-randomnick")) {
		            		String[] names = {"Malfurion",
		            				"Rexxar",
		            				"Jaina",
		            				"Uther",
		            				"Anduin",
		            				"Valeera",
		            				"Thrall",
		            				"Gul'dan",
		            				"Garrosh",
		            				"Medivh",
		            				"Dildo",
		            				"2B",
		            				"Toba"};
		            		
		            		Random ran = new Random();
		            		String tempName;
		            		while(true) {
		            			tempName = names[ran.nextInt(names.length)] + " Bot";
		            			if(castedEvent.getGuild().getSelfMember().getNickname() == null || !castedEvent.getGuild().getSelfMember().getNickname().equals(tempName)) {
		            				break;
		            			}
		            		}
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getGuild().getSelfMember(), tempName).queue();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + tempName).queue();
		            		
		            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		            	} else if (message.startsWith("-clear ")) {
		            		String param = message.substring(7);
		            		try {
		            			int i = Integer.parseInt(param);
		            			if(!Cleaner.isRunning()) {
		            				Thread clearProcess = new Thread(new Cleaner(castedEvent.getChannel(), i));
				            		clearProcess.start();
				            		
				            		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		            			}
		            			// No point on sending a message that the cleaner is already running since it will get instantly deleted.
		            		} catch(NumberFormatException e) {
		            			castedEvent.getChannel().sendMessage("**Please enter a correct number!**").queue();
		            			
		            			logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
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
