package com.dpain.DiscordBot.helper;

import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;

public class LogHelper {
	
	/**
	 * Returns a string in a formatted fashion for GuildMessageReceivedEvent.
	 * @param event
	 * @param line
	 * @return
	 */
	public static String elog(GuildMessageReceivedEvent event, String line) {
		return String.format("Member: %s (username: %s - %s) in guild: %s\nDescription: %s",
				event.getMember().getEffectiveName(),
				event.getAuthor().getName(),
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
	 * Returns a string in a formatted fashion for UserGameUpdateEvent.
	 * @param level Level enum.
	 * @param event Event raised.
	 * @param line Description.
	 */
	public static String elog(UserGameUpdateEvent event, String line) {
		return String.format("Member: %s (username: %s - %s) in guild: %s\nDescription: %s",
						event.getGuild().getMemberById(event.getUser().getId()).getEffectiveName(),
						event.getUser().getName(),
						event.getUser().getId(),
						event.getGuild().getName(),
						line);
	}
}
