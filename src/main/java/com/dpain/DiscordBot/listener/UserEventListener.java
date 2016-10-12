package com.dpain.DiscordBot.listener;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.listener.twitch.TwitchAlerter;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.UserManager;

import net.dv8tion.jda.entities.Game;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberUnbanEvent;
import net.dv8tion.jda.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.hooks.EventListener;

public class UserEventListener implements EventListener {
	private String name;
	private static TwitchAlerter alerter;
	private static Guild guild;
	
	public UserEventListener() {
		name = "UserEventListener";
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Listening started!"));
	}
	
    @Override
    public void onEvent(Event event) {
        if(event instanceof GuildMemberJoinEvent) {
        	GuildMemberJoinEvent castedEvent = (GuildMemberJoinEvent) event;
        	
        	UserManager.load().addNewUser(castedEvent.getUser());
        	
        	castedEvent.getGuild().getPublicChannel().sendMessage("Hi, " + castedEvent.getUser().getUsername() + "!\nWelcome to the VGTC Discord Server!");
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
        } else if(event instanceof UserGameUpdateEvent) {
        	UserGameUpdateEvent castedEvent = (UserGameUpdateEvent) event;
        	if(castedEvent.getUser().getCurrentGame() != null) {
        		String temp = castedEvent.getUser().getCurrentGame().getUrl();
        		if(temp != null && Game.isValidStreamingUrl(temp)) {
    				if(isTrustedTwitchStreamer(castedEvent.getUser()) && !castedEvent.getUser().getId().equals(event.getJDA().getSelfInfo().getId())) {
    					alerter.notifyTwitchStream(castedEvent.getUser());
						System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getUsername() + ", " + castedEvent.getUser().getId() + "] is streaming in Twitch."));
    				}
    			}
        	}
		}
    }
    
    private boolean isTrustedTwitchStreamer(User user) {
    	return UserManager.load().getUserGroup(user).getHierarchy() <= Group.TRUSTED_USER.getHierarchy();
    }
    
    public static void setDefaultGuild(Guild guild) {
    	UserEventListener.guild = guild;
    	instantiateTwitchAlerter();
	}
    
    public static void instantiateTwitchAlerter() {
    	alerter = new TwitchAlerter(guild);
    }
}