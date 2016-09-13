package com.dpain.DiscordBot;

import javax.security.auth.login.LoginException;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.CommandListener;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.InviteListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

public class DiscordBot {
	private JDA jda;
	
	CommandListener commandListener;
	
	public DiscordBot() {
		
		try {
			commandListener = new CommandListener();
			 
			//Chain listeners if adding more
			jda = new JDABuilder().setBotToken(PropertiesManager.load().getValue(Property.BOT_TOKEN)).addListener(new InviteListener()).addListener(commandListener).addListener(new UserEventListener()).buildBlocking();
			jda.getAccountManager().setGame("Bot Activated!");
			
			UserManager.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			UserManager.load();
			
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("DiscordBot", "Running!"));
		} catch (LoginException e) {
			System.out.println("The provided Login information is incorrect. Please provide valid details.");
		} catch (IllegalArgumentException e) {
			System.out.println("The config was not populated. Please enter an email and password.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void readConsole() {
		(new Thread(new ConsoleInputReader(jda, commandListener, jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID))))).start();
	}
}
