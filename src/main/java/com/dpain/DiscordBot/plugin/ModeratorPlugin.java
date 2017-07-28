package com.dpain.DiscordBot.plugin;

import java.util.Random;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.plugin.moderator.Cleaner;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ModeratorPlugin extends Plugin {

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
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getMember(), param).complete();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param);
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
		            				"Dildo"};
		            		
		            		Random ran = new Random();
		            		String tempName;
		            		while(true) {
		            			tempName = names[ran.nextInt(names.length)] + " Bot";
		            			if(castedEvent.getGuild().getSelfMember().getNickname() == null || !castedEvent.getGuild().getSelfMember().getNickname().equals(tempName)) {
		            				break;
		            			}
		            		}
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getGuild().getSelfMember(), tempName).complete();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + tempName);
		            		
		            	} else if (message.startsWith("-clear ")) {
		            		String param = message.substring(7);
		            		try {
		            			int i = Integer.parseInt(param);
		            			if(!Cleaner.isRunning()) {
		            				Thread clearProcess = new Thread(new Cleaner(castedEvent.getChannel(), i));
				            		clearProcess.start();
		            			}
		            		} catch(NumberFormatException e) {
		            			castedEvent.getChannel().sendMessage("**Please enter a correct number!**");
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
