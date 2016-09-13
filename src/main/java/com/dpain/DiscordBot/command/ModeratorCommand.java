package com.dpain.DiscordBot.command;

import java.util.Random;

import com.dpain.DiscordBot.command.moderator.Cleaner;
import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class ModeratorCommand extends Command {

	public ModeratorCommand() {
		super("ModeratorCommand", Group.MODERATOR);
		
		super.helpString = "**Moderator Command Usage:**\n"
				+ "-nick *\"name\"* : Changes the nickname of the bot.\n"
				+ "-randomnick : Randomly changes the nickname of the bot.\n"
				+ "-clear *\"channelName\"* : Clears all the messages in the text channel\n";
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
		                if (message.startsWith("-nick ")) {
		            		String param = message.substring(6);
		            		
		            		castedEvent.getGuild().getManager().setNickname(castedEvent.getJDA().getSelfInfo(), param);
		            		castedEvent.getJDA().getAccountManager().update();
		            		
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
		            				"Medivh"};
		            		
		            		Random ran = new Random();
		            		String tempName;
		            		while(true) {
		            			tempName = names[ran.nextInt(names.length)] + " Bot";
		            			if(castedEvent.getGuild().getNicknameForUser(castedEvent.getJDA().getSelfInfo()) == null || !castedEvent.getGuild().getNicknameForUser(castedEvent.getJDA().getSelfInfo()).equals(tempName)) {
		            				break;
		            			}

		            		}
		            		
		            		castedEvent.getGuild().getManager().setNickname(castedEvent.getJDA().getSelfInfo(), tempName);
		            		castedEvent.getJDA().getAccountManager().update();

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
