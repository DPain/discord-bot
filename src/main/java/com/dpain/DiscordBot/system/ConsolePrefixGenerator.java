package com.dpain.DiscordBot.system;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsolePrefixGenerator {
	public static String getFormattedPrintln(String name, String line) {
		return "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString() + "] [Info] [" + name +"]: " + line;
	}
}
