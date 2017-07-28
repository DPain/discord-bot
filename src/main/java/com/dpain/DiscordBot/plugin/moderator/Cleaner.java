package com.dpain.DiscordBot.plugin.moderator;

import java.util.List;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

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
		
		List<Message> messages = messageHistory.getRetrievedHistory();
		int i = 0;
		while(messages != null && i < count) {
			for(Message item: messages)	 {

				if(i >= count) {
					System.out.println(ConsolePrefixGenerator.getFormattedPrintln("Cleaner", "Deleted " + (count - 1) + " messages."));
					isRunning = false;
					return;
				}
				
				item.delete();
				i++;
	        }
	        if(messages.isEmpty())
	            messages = messageHistory.getRetrievedHistory();
	        if(messages == null) {
	        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln("Cleaner", "No more messages left."));
	        	isRunning = false;
	        	return;
	        }
	        
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
}
