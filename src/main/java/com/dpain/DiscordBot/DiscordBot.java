package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.listener.g2g.G2gAlerter;
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
	private final static Logger logger = LoggerFactory.getLogger(DiscordBot.class);
	private JDA jda;

	PluginListener pluginListener;

	public DiscordBot() {

		try {
			// Initialized before PluginListener since a plugin might rely on some
			// properties.
			PropertiesManager.load();

			pluginListener = new PluginListener();

			JDABuilder builder = new JDABuilder(AccountType.BOT)
					.setToken(PropertiesManager.load().getValue(Property.BOT_TOKEN));

			builder.addEventListener(pluginListener);
			builder.addEventListener(new UserEventListener());

			jda = builder.build().awaitReady();
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, "Bot Activated!"));

			MemberManager.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			MemberManager.load();
			UserEventListener.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			
			G2gAlerter.load().setJDA(jda);
            // Set thread for automated g2g price check and accouncment.

			logger.info("Registered Guilds:");
			for (Guild guild : jda.getGuilds()) {
				logger.info(String.format("Name: %s id: %s: ", guild.getName(), guild.getId()));
			}
			logger.info("Bot is running!");
		} catch (LoginException e) {
			logger.error("The provided Login information is incorrect. Please provide valid details.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// changeAvatar();

	}

	public void readConsole() {
		(new Thread(new ConsoleInputReader(jda, pluginListener,
				jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID))))).start();
	}

	private void changeAvatar() {
		Icon icon = null;
		try {
			icon = Icon.from(new File("File Path"));
		} catch (IOException e) {
			logger.error("The image file does not exist!");
		}
		jda.getSelfUser().getManager().setAvatar(icon).complete();
	}
}
