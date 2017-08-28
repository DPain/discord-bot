package com.dpain.DiscordBot.plugin;

import java.util.Random;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.plugin.moderator.Cleaner;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

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
		            		
		            		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(),
                					String.format("Nickname changed! member: %s (username: %s) at channel: %s in guild: %s\nNickname: %s",
                							castedEvent.getMember().getEffectiveName(),
                							castedEvent.getAuthor().getName(),
                							castedEvent.getChannel().getName(),
                							castedEvent.getChannel().getGuild().getName(),
                							param)));
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getMember(), param).queue();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param).queue();
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
		            		
		            		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(),
                					String.format("Random nickname changed! member: %s (username: %s) at channel: %s in guild: %s\nNickname: %s",
                							castedEvent.getMember().getEffectiveName(),
                							castedEvent.getAuthor().getName(),
                							castedEvent.getChannel().getName(),
                							castedEvent.getChannel().getGuild().getName(),
                							tempName)));
		            		
		            		castedEvent.getGuild().getController().setNickname(castedEvent.getGuild().getSelfMember(), tempName).queue();
		            		castedEvent.getChannel().sendMessage("**Nickname changed to:** " + tempName).queue();
		            		
		            	} else if (message.startsWith("-clear ")) {
		            		String param = message.substring(7);
		            		try {
		            			int i = Integer.parseInt(param);
		            			if(!Cleaner.isRunning()) {
		            				System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(),
		                					String.format("Deleting messages! member: %s (username: %s) at channel: %s in guild: %s\nAmount: %d",
		                							castedEvent.getMember().getEffectiveName(),
		                							castedEvent.getAuthor().getName(),
		                							castedEvent.getChannel().getName(),
		                							castedEvent.getChannel().getGuild().getName(),
		                							i)));
		            				
		            				Thread clearProcess = new Thread(new Cleaner(castedEvent.getChannel(), i));
				            		clearProcess.start();
		            			}
		            			// No point on sending a message that the cleaner is already running since it will get instantly deleted.
		            		} catch(NumberFormatException e) {
		            			castedEvent.getChannel().sendMessage("**Please enter a correct number!**").queue();
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
