package com.dpain.DiscordBot.plugin.reminder;

import java.util.TimerTask;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.entities.TextChannel;

public class Reminder extends TimerTask {

	private TextChannel channel;
	private String description;
	
	/**
	 * Constructor
	 * @param channel
	 * @param description
	 */
	public Reminder(TextChannel channel, String description) {
		this.channel = channel;
		this.description = description;
	}
	
	@Override
	public void run() {
		channel.sendMessage("Reminder: " + description);
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("Reminder", "Reminded the channel: " + channel.getName() + "\nDescription: " + description));
	}
	
}
