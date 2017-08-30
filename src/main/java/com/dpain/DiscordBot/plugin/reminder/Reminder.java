package com.dpain.DiscordBot.plugin.reminder;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Reminder extends TimerTask {
	private final static Logger logger = Logger.getLogger(Reminder.class.getName());
	
	private User who;
	private TextChannel channel;
	private String description;
	
	/**
	 * Constructor
	 * @param channel
	 * @param description
	 */
	public Reminder(User who, TextChannel channel, String description) {
		this.who = who;
		this.channel = channel;
		this.description = description;
	}
	
	@Override
	public void run() {
		channel.sendMessage(String.format("%s Reminder: %s",
				channel.getGuild().getMember(who).getEffectiveName(),
				description)).queue();
		logger.log(Level.INFO,
				String.format("Reminded member: %s (username: %s) at channel: %s in guild: %s\nDescription: %s",
						channel.getGuild().getMember(who).getEffectiveName(),
						who.getName(),
						channel.getName(),
						channel.getGuild().getName(),
						description));
	}
}
