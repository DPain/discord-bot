package com.dpain.DiscordBot.helper;

import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;

public class LogHelper {

  /**
   * Returns a string in a formatted fashion for SlashCommandEvent.
   * 
   * @param event
   * @param line
   * @return
   */
  public static String elog(SlashCommandEvent event, String line) {
    return String.format("User: %s (nickname: %s - %s) in guild: %s\nDescription: %s",
        event.getMember().getUser().getName(), event.getMember().getNickname(), event.getMember().getUser().getId(),
        event.getGuild().getName(), line);
  }
  
  /**
   * Returns a string in a formatted fashion for GuildMessageReceivedEvent.
   * 
   * @param event
   * @param line
   * @return
   */
  public static String elog(GuildMessageReceivedEvent event, String line) {
    return String.format("User: %s (nickname: %s - %s) in guild: %s\nDescription: %s",
        event.getMember().getUser().getName(), event.getMember().getNickname(), event.getMember().getUser().getId(),
        event.getGuild().getName(), line);
  }

  /**
   * Returns a string in a formatted fashion for GuildBanEvent.
   * 
   * @param event
   * @param line
   * @return
   */
  public static String elog(GuildBanEvent event, String line) {
    return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
        event.getUser().getName(), event.getUser().getId(), event.getGuild().getName(), line);
  }

  /**
   * Returns a string in a formatted fashion for GenericGuildMemberEvent.
   * 
   * @param event
   * @param line
   * @return
   */
  public static String elog(GenericGuildMemberEvent event, String line) {
    return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
        event.getUser().getName(), event.getUser().getId(), event.getGuild().getName(), line);
  }
  
  /**
   * Returns a string in a formatted fashion for GuildMemberRemoveEvent.
   * 
   * @param event
   * @param line
   * @return
   */
  public static String elog(GuildMemberRemoveEvent event, String line) {
    return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
        event.getUser().getName(), event.getUser().getId(), event.getGuild().getName(), line);
  }

  /**
   * Returns a string in a formatted fashion for GuildJoinEvent.
   * 
   * @param event Event raised.
   * @param line Description.
   */
  public static String elog(GuildJoinEvent event, String line) {
    return String.format("Bot joined the guild: %s (id: %s)\nDescription: %s",
        event.getGuild().getName(), event.getGuild().getId(), line);
  }
  
  /**
   * Returns a string in a formatted fashion for GuildUnbanEvent.
   * 
   * @param event Event raised.
   * @param line Description.
   */
  public static String elog(GuildUnbanEvent event, String line) {
    return String.format("User: %s (id: %s) in guild: %s\nDescription: %s",
        event.getUser().getName(), event.getUser().getId(), event.getGuild().getName(), line);
  }


  /**
   * Returns a string in a formatted fashion for GenericUserPresenceEvent.
   * 
   * @param event Event raised.
   * @param line Description.
   */
  public static String elog(GenericUserPresenceEvent event, String line) {
    return String.format("Member: %s (username: %s - %s) in guild: %s\nDescription: %s",
        event.getGuild().getMemberById(event.getMember().getId()).getEffectiveName(),
        event.getMember().getNickname(), event.getMember().getId(), event.getGuild().getName(), line);
  }
}
