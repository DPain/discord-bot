package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.security.auth.login.LoginException;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.system.MemberManager;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class DiscordBot {
	private JDA jda;
	
	PluginListener pluginListener;
	
	public DiscordBot() {
		
		try {
			pluginListener = new PluginListener(jda);
			 
			//Chain listeners if adding more
			jda = new JDABuilder(AccountType.BOT).setBulkDeleteSplittingEnabled(false).setToken(PropertiesManager.load().getValue(Property.BOT_TOKEN)).addEventListener(pluginListener).addEventListener(new UserEventListener()).buildBlocking();
			jda.getPresence().setGame(Game.of("Bot Activated!"));
			
			MemberManager.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			MemberManager.load();
			UserEventListener.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("DiscordBot", "Registered Guilds:"));
			for(Guild guild : jda.getGuilds()) {
				System.out.println(ConsolePrefixGenerator.getFormattedPrintln("DiscordBot", "Name: " + guild.getName() + " id: " + guild.getId()));
			}
			
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("DiscordBot", "Running!"));
			
			
		} catch (LoginException e) {
			System.out.println("The provided Login information is incorrect. Please provide valid details.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RateLimitedException e) {
			System.out.println("There were too many requests. Please try again later.");
		}
		//changeAvatar();
	}
	
	public void readConsole() {
		(new Thread(new ConsoleInputReader(jda, pluginListener, jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID))))).start();
	}
	
	private void changeAvatar() {
		Icon icon = null;
		try {
			icon = Icon.from(new File("File Path"));
		} catch (IOException e) {
			System.out.println("The image file does not exist!");
		}
		jda.getSelfUser().getManager().setAvatar(icon).complete();
	}
}
