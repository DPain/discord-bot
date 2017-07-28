package com.dpain.DiscordBot.listener;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.twitch.TwitchAlerter;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.MemberManager;
import com.dpain.DiscordBot.system.PropertiesManager;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class UserEventListener implements net.dv8tion.jda.core.hooks.EventListener {
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
        	
        	MemberManager.load().addNewMember(guild.getMember(castedEvent.getUser()));
        	
        	castedEvent.getGuild().getPublicChannel().sendMessage("Hi, " + castedEvent.getUser().getName() + "!\nWelcome to the Discord Server!");
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getName() + ", " + castedEvent.getUser().getId() + "] joined the guild."));
        } else if(event instanceof GuildBanEvent) {
        	GuildBanEvent castedEvent = (GuildBanEvent) event;
        	
        	MemberManager.load().changeMemberGroup(guild.getMember(castedEvent.getUser()), Group.PRISONER);
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getName() + ", " + castedEvent.getUser().getId() + "] is banned."));
        } else if(event instanceof GuildMemberLeaveEvent) {
        	GuildMemberLeaveEvent castedEvent = (GuildMemberLeaveEvent) event;
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getName() + ", " + castedEvent.getUser().getId() + "] left the guild."));
        } else if(event instanceof GuildBanEvent) {
        	GuildUnbanEvent castedEvent = (GuildUnbanEvent) event;
        	
        	System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getName() + ", " + castedEvent.getUser().getId() + "] is unbanned."));
        } else if(event instanceof UserGameUpdateEvent) {
        	UserGameUpdateEvent castedEvent = (UserGameUpdateEvent) event;
        	if(castedEvent.getGuild().getMember(castedEvent.getUser()).getGame() != null) {
        		String temp = castedEvent.getGuild().getMember(castedEvent.getUser()).getGame().getUrl();
        		if(temp != null && Game.isValidStreamingUrl(temp)) {
    				if(isTrustedTwitchStreamer(guild.getMember(castedEvent.getUser())) && !castedEvent.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
    					if(PropertiesManager.load().getValue(Property.USE_TWITCH_ALERTER).equals("true")) {
    						alerter.notifyTwitchStream(guild.getMember(castedEvent.getUser()));
    					}
						System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "User[" + castedEvent.getUser().getName() + ", " + castedEvent.getUser().getId() + "] is streaming in Twitch."));
    				}
    			}
        	}
		}
    }
    
    private boolean isTrustedTwitchStreamer(Member member) {
    	return MemberManager.load().getMemberGroup(member).getHierarchy() <= Group.TRUSTED_USER.getHierarchy();
    }
    
    public static void setDefaultGuild(Guild guild) {
    	UserEventListener.guild = guild;
    	instantiateTwitchAlerter();
	}
    
    public static void instantiateTwitchAlerter() {
    	alerter = new TwitchAlerter(guild);
    }
}