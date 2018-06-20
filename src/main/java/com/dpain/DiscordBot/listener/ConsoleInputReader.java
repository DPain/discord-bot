package com.dpain.DiscordBot.listener;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.Main;
import com.dpain.DiscordBot.helper.LogHelper;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;

public class ConsoleInputReader implements Runnable {
	private final static Logger logger = Logger.getLogger(ConsoleInputReader.class.getName());
	
	private JDA jda;
	private Guild processingGuild;
	private Scanner in;
	
	public ConsoleInputReader(JDA jda, PluginListener listener, Guild guild) {
		this.jda = jda;
		
		processingGuild = guild;
		in = new Scanner(System.in);
		
		logger.log(Level.INFO, "Starts reading input from console!");
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
			processingGuild.getDefaultChannel().sendMessage(messageBuilder.build());
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
			logger.log(Level.INFO, "Discord Bot Plugin: ");
			String commandLine = in.nextLine();
			if(!processConsoleCommand(commandLine)) {
				break outerWhile;
			}
		}
	}
}
