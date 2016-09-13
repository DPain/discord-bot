package com.dpain.DiscordBot.listener;

import java.util.LinkedList;

import com.dpain.DiscordBot.command.AnimeCommand;
import com.dpain.DiscordBot.command.AudioPlayerCommand;
import com.dpain.DiscordBot.command.Command;
import com.dpain.DiscordBot.command.CustomCommand;
import com.dpain.DiscordBot.command.EssentialsCommand;
import com.dpain.DiscordBot.command.ModeratorCommand;
import com.dpain.DiscordBot.command.OwnerCommand;
import com.dpain.DiscordBot.command.ProfanityGuardCommand;
import com.dpain.DiscordBot.command.WeatherCommand;
import com.dpain.DiscordBot.command.WikipediaCommand;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.hooks.EventListener;

public class CommandListener implements EventListener {
	private String name;
	private LinkedList<Command> commands;
	
	public CommandListener() {
		name = "CommandListener";
		
		//Maybe use one hashmap and have each commands to add into the hashmap. 
		commands = new LinkedList<Command>();
		
		commands.add(new AnimeCommand());
		commands.add(new AudioPlayerCommand());
		//commands.add(new CustomCommand());
		commands.add(new EssentialsCommand());
		commands.add(new ModeratorCommand());
		commands.add(new OwnerCommand());
		commands.add(new ProfanityGuardCommand());
		commands.add(new WeatherCommand());
		commands.add(new WikipediaCommand());
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Listening started!"));
	}
	
	public void onEvent(Event event) {
		for(Command command : commands) {
        	command.handleEvent(event);
        }
	}
}
