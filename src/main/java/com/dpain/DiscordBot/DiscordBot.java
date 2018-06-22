package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.system.MemberManager;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Game.GameType;

public class DiscordBot {
	private final static Logger logger = Logger.getLogger(DiscordBot.class.getName());
	private JDA jda;
	
	PluginListener pluginListener;
	
	public DiscordBot() {
		
		try {
			// Initialized before PluginListener since a plugin might rely on some properties.
			PropertiesManager.load();
			
			pluginListener = new PluginListener(jda);
			 
			// Chain listeners if adding more
			jda = new JDABuilder(AccountType.BOT).setBulkDeleteSplittingEnabled(false).setToken(PropertiesManager.load().getValue(Property.BOT_TOKEN)).addEventListener(pluginListener).addEventListener(new UserEventListener()).buildBlocking();
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, "Bot Activated!"));
			
			MemberManager.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			MemberManager.load();
			UserEventListener.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			
			logger.log(Level.INFO, "Registered Guilds:");
			for(Guild guild : jda.getGuilds()) {
				logger.log(Level.INFO,
						String.format("Name: %s id: %s: ", guild.getName(), guild.getId()));
			}
			logger.log(Level.INFO, "Running!");
		} catch (LoginException e) {
			System.out.println("The provided Login information is incorrect. Please provide valid details.");
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			logger.log(Level.SEVERE, "The image file does not exist!");
		}
		jda.getSelfUser().getManager().setAvatar(icon).complete();
	}
}
