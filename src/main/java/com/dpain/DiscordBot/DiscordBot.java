package com.dpain.DiscordBot;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.security.auth.login.LoginException;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.InviteListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.utils.AvatarUtil;
import net.dv8tion.jda.utils.AvatarUtil.Avatar;

public class DiscordBot {
	private JDA jda;
	
	PluginListener pluginListener;
	
	public DiscordBot() {
		
		try {
			pluginListener = new PluginListener(jda);
			 
			//Chain listeners if adding more
			jda = new JDABuilder().setBotToken(PropertiesManager.load().getValue(Property.BOT_TOKEN)).addListener(new InviteListener()).addListener(pluginListener).addListener(new UserEventListener()).buildBlocking();
			jda.getAccountManager().setGame("Bot Activated!");
			
			UserManager.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			UserManager.load();
			UserEventListener.setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
			
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("DiscordBot", "Running!"));
			
			//changeAvatar();
		} catch (LoginException e) {
			System.out.println("The provided Login information is incorrect. Please provide valid details.");
		} catch (IllegalArgumentException e) {
			System.out.println("The config was not populated. Please enter an email and password.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void readConsole() {
		(new Thread(new ConsoleInputReader(jda, pluginListener, jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID))))).start();
	}
	
	private void changeAvatar() {
		Avatar avatar;
		try {
			avatar = AvatarUtil.getAvatar(new File("File Path"));
			jda.getAccountManager().setAvatar(avatar);
			jda.getAccountManager().update();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
