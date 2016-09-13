package com.dpain.DiscordBot.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Properties;

import com.dpain.DiscordBot.enums.Property;

public class PropertiesManager {
	private static PropertiesManager ref;
	
	private Properties settings;
	
	private PropertiesManager() {
		try {
			File configFile = new File("bot.properties");
			
			if(!configFile.exists()) {
				setupPropertiesFile();
			}
			
			settings = new Properties();
			settings.load(new FileReader(configFile));
		} catch (FileNotFoundException e) {
			System.out.println("The bot.properties does not exist and could not be created!");
		} catch (IOException e) {
			System.out.println("The bot.properties cannot be readed!");
		}
	}

	public static PropertiesManager loadPropertiesManager() {
		if(ref == null) {
			ref = new PropertiesManager();
		}
		return ref;
	}
	
	/**
	 * Just a shorter version for loadPropertiesManager().
	 * @return a singleton of PropertiesManager
	 */
	public static PropertiesManager load() {
		return loadPropertiesManager();
	}
	
	public String getValue(Property property) {
		return settings.getProperty(property.getKey());
	}
	
	private void setupPropertiesFile() throws IOException {
		LocalDateTime timePoint = LocalDateTime.now();
		
		File configFile = new File("bot.properties");
		configFile.createNewFile();
		
		String line = "#Properties file created at: " + timePoint.toString() + "\n" +
				Property.USERNAME.getKey() + "=\n" + 
				Property.ACCEPT_INVITES.getKey() + "=true\n" + 
				Property.BOT_ID.getKey() + "=\n" + 
				Property.BOT_TOKEN.getKey() + "=\n" + 
				Property.APP_ID.getKey() + "=\n" + 
				Property.OWNER_USER_ID.getKey() + "=\n" + 
				Property.GUILD_ID.getKey() + "=\n" + 
				Property.WEATHER_API_KEY.getKey() + "=\n";
		
		PrintStream printer = new PrintStream(configFile);
		printer.print(line);
		printer.close();
	}
}
