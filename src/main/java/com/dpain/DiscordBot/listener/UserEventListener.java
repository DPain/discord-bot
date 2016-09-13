package com.dpain.DiscordBot.listener;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberUnbanEvent;
import net.dv8tion.jda.hooks.EventListener;

public class UserEventListener implements EventListener {
	private String name;
	
	public UserEventListener() {
		name = "UserEventListener";
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Listening started!"));
	}
	
    @Override
    public void onEvent(Event event) {
        if(event instanceof GuildMemberJoinEvent) {
        	GuildMemberJoinEvent castedEvent = (GuildMemberJoinEvent) event;
        	
        	UserManager.load().addNewUser(castedEvent.getUser());
        	
        	castedEvent.getGuild().getPublicChannel().sendMessage("Hi, " + castedEvent.getUser().getUsername() + "!\nWelcome to the UDVGTC Discord Server!");
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getUsername() + ", " + castedEvent.getUser().getId() + "] joined the guild."));
        } else if(event instanceof GuildMemberBanEvent) {
        	GuildMemberBanEvent castedEvent = (GuildMemberBanEvent) event;
        	
        	UserManager.load().changeUserGroup(castedEvent.getUser(), Group.PRISONER);
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getUsername() + ", " + castedEvent.getUser().getId() + "] is banned."));
        } else if(event instanceof GuildMemberLeaveEvent) {
        	GuildMemberLeaveEvent castedEvent = (GuildMemberLeaveEvent) event;
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getUsername() + ", " + castedEvent.getUser().getId() + "] left the guild."));
        } else if(event instanceof GuildMemberUnbanEvent) {
        	GuildMemberUnbanEvent castedEvent = (GuildMemberUnbanEvent) event;
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getUsername() + ", " + castedEvent.getUser().getId() + "] is unbanned."));
        }
    }
}