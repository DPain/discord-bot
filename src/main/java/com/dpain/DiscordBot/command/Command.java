package com.dpain.DiscordBot.command;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.Event;

public abstract class Command {
	private final String name;
	private Group group;
	protected String helpString;
	
	public Command(String name, Group group) {
		this.name = name;
		this.group = group;
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Initialized!"));
	}
	
	public abstract void handleEvent(Event event);

	public final String getHelpSpring() {
		return helpString;
	}
	
	public final String getName() {
		return name;
	}
	
	protected boolean canAccessCommand(User user) {
		return UserManager.load().getUserGroup(user).getHierarchy() <= group.getHierarchy();
	}
}
