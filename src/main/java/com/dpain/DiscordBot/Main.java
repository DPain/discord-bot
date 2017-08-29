package com.dpain.DiscordBot;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
	private final static Logger logger = Logger.getLogger(Main.class.getName());
	private static FileHandler fileHandler = null;

	public static void main(String[] args) {
		try {
			fileHandler = new FileHandler("log.log", false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		
		Logger logger = Logger.getLogger("");
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
		logger.setLevel(Level.CONFIG);
		
		DiscordBot myBot = new DiscordBot();
		myBot.readConsole();
	}
}
