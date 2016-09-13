package com.dpain.DiscordBot.listener;

import java.util.Scanner;

import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.entities.impl.MessageImpl;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class ConsoleInputReader implements Runnable {
	private String name;
	private JDA jda;
	private CommandListener listener;
	private Guild processingGuild;
	private Scanner in;
	
	public ConsoleInputReader(JDA jda, CommandListener listener, Guild guild) {
		this.name = "ConsoleInputReader";
		this.jda = jda;
		this.listener = listener;
		
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
								"-changeguild [guild id] = Changes the guild the bot will forward the commands\n" + 
								"");
		} else {
			MessageImpl messageImpl = new MessageImpl("", (JDAImpl) jda);
			messageImpl.setContent(commandLine);
			
			GuildMessageReceivedEvent event = new GuildMessageReceivedEvent(jda, jda.getResponseTotal(), (Message) messageImpl, processingGuild.getPublicChannel());
			
			listener.onEvent(event);
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
			System.out.println("Discord Bot Command: ");
			String commandLine = in.nextLine();
			if(!processConsoleCommand(commandLine)) {
				break outerWhile;
			}
		}
	}
}
