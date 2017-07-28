package com.dpain.DiscordBot.listener;

import java.util.Scanner;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;

public class ConsoleInputReader implements Runnable {
	private String name;
	private JDA jda;
	private Guild processingGuild;
	private Scanner in;
	
	public ConsoleInputReader(JDA jda, PluginListener listener, Guild guild) {
		this.name = "ConsoleInputReader";
		this.jda = jda;
		
		processingGuild = guild;
		in = new Scanner(System.in);
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Starts reading input from console!"));
	}
	
	private boolean processConsoleCommand(String commandLine) {
		if(commandLine.equals("-exit")) {
			jda.shutdown();
			System.exit(0);
			return false;
		} else if(commandLine.startsWith("-changeguild ")) {
			String guildId = commandLine.substring(13);
			changeGuildById(guildId);
		} else if(commandLine.equals("-help")) {
			System.out.println("Commands:\n" + 
								"-exit = Terminates the bot\n" + 
								"-changeguild [guild id] = Changes the guild the bot will forward the commands\n");
		} else {
			MessageBuilder messageBuilder = new MessageBuilder();
			messageBuilder.append(commandLine);
			processingGuild.getPublicChannel().sendMessage(messageBuilder.build());
		}
		return true;
	}
	
	private void changeGuildById(String id) {
		processingGuild = jda.getGuildById(id);
	}
	
	public void run() {
		//Might have to fix
		outerWhile:
		while(true) {
			System.out.println("Discord Bot Plugin: ");
			String commandLine = in.nextLine();
			if(!processConsoleCommand(commandLine)) {
				break outerWhile;
			}
		}
	}
}
