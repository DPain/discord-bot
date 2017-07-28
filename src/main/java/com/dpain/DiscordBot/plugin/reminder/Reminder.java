package com.dpain.DiscordBot.plugin.reminder;

import java.util.TimerTask;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.core.entities.TextChannel;

public class Reminder extends TimerTask {
	private String who;
	private TextChannel channel;
	private String description;
	
	/**
	 * Constructor
	 * @param channel
	 * @param description
	 */
	public Reminder(String who, TextChannel channel, String description) {
		this.who = who;
		this.channel = channel;
		this.description = description;
	}
	
	@Override
	public void run() {
		channel.sendMessage(who + " Reminder: " + description);
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("Reminder", "Reminded " + who + " at channel: " + channel.getName()
		+ "\nDescription: " + description));
	}
	
}
