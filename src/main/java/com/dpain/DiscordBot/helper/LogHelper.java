package com.dpain.DiscordBot.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.system.PropertiesManager;

import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.events.user.update.GenericUserPresenceEvent;

public class LogHelper {
	private final static Logger logger = Logger.getLogger(PluginListener.class.getName());
	
	/**
	 * Returns a string in a formatted fashion for GuildMessageReceivedEvent.
	 * @param event
	 * @param line
	 * @return
	 */
	public static String elog(GuildMessageReceivedEvent event, String line) {
		return String.format("User: %s (nickname: %s - %s) in guild: %s\nDescription: %s",
				event.getAuthor().getName(),
				event.getMember().getNickname(),
				event.getAuthor().getId(),
				event.getGuild().getName(),
				line);
	}
	
	/**
	 * Returns a string in a formatted fashion for GuildBanEvent.
	 * @param event
	 * @param line
	 * @return
	 */
	public static String elog(GuildBanEvent event, String line) {
		return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
				event.getUser().getName(),
				event.getUser().getId(),
				event.getGuild().getName(),
				line);
	}
	
	/**
	 * Returns a string in a formatted fashion for GenericGuildMemberEvent.
	 * @param event
	 * @param line
	 * @return
	 */
	public static String elog(GenericGuildMemberEvent event, String line) {
		return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
				event.getUser().getName(),
				event.getUser().getId(),
				event.getGuild().getName(),
				line);
	}
	
	/**
	 * Returns a string in a formatted fashion for GuildUnbanEvent.
	 * @param level Level enum.
	 * @param event Event raised.
	 * @param line Description.
	 */
	public static String elog(GuildUnbanEvent event, String line) {
		return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
						event.getUser().getName(),
						event.getUser().getId(),
						event.getGuild().getName(),
						line);
	}

	/**
	 * Returns a string in a formatted fashion for GenericUserPresenceEvent.
	 * @param level Level enum.
	 * @param event Event raised.
	 * @param line Description.
	 */
	public static String elog(GenericUserPresenceEvent event, String line) {
		return String.format("Member: %s (username: %s - %s) in guild: %s\nDescription: %s",
						event.getGuild().getMemberById(event.getUser().getId()).getEffectiveName(),
						event.getUser().getName(),
						event.getUser().getId(),
						event.getGuild().getName(),
						line);
	}
	
	public static void logToChannel() {
		if(PropertiesManager.load().getValue(Property.LOGGER_CHANNEL_ID) != "") {
			try {
				
			} catch(Exception e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}
}
