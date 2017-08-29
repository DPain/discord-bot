package com.dpain.DiscordBot.plugin.moderator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;

import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class Cleaner implements Runnable {
	private final static Logger logger = Logger.getLogger(Cleaner.class.getName());
	
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
					logger.log(Level.INFO, String.format("Deleted %d messages.", count - 1));
					isRunning = false;
					return;
				}
				
				item.delete();
				i++;
	        }
	        if(messages.isEmpty())
	            messages = messageHistory.getRetrievedHistory();
	        if(messages == null) {
	        	logger.log(Level.INFO, "No more messages left.");
	        	isRunning = false;
	        	return;
	        }
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
}
