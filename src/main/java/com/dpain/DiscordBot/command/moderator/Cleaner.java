package com.dpain.DiscordBot.command.moderator;

import java.util.List;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;

public class Cleaner implements Runnable {
	private static boolean isRunning = false;
	
	private MessageHistory messageHistory;
	private int count;
	
	public Cleaner(TextChannel targetChannel, int i) {
		messageHistory = new MessageHistory(targetChannel);
		count = i + 1;
	}

	@Override
	public void run() {
		isRunning = true;
		
		List<Message> messages = messageHistory.retrieve();
		int i = 0;
		while(messages != null && i < count) {
			for(Message item: messages)	 {

				if(i >= count) {
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistHandler", "Deleted " + (count - 1) + " messages."));
					isRunning = false;
					return;
				}
				
				item.deleteMessage();
				i++;
	        }
	        if(messages.isEmpty())
	            messages = messageHistory.retrieve();
	        if(messages == null) {
	        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("PlaylistHandler", "No more messages left."));
	        	isRunning = false;
	        	return;
	        }
	        
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
}
