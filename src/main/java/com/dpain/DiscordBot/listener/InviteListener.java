package com.dpain.DiscordBot.listener;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.PropertiesManager;

import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.InviteReceivedEvent;
import net.dv8tion.jda.hooks.EventListener;

public class InviteListener implements EventListener {
	private String name;
	
	public InviteListener() {
		name = "InviteListener";
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Listening started!"));
	}
	
    @Override
    public void onEvent(Event e) {
        if(e instanceof InviteReceivedEvent) {
            InviteReceivedEvent event = (InviteReceivedEvent) e;
            /*
            if(BlackList.contains(event.getAuthor())) {
                return;
            }
            */
            MessageChannel channel = event.isPrivate() ? event.getJDA().getPrivateChannelById(event.getMessage().getChannelId()) :
                    event.getJDA().getTextChannelById(event.getMessage().getChannelId());
            if((event.isPrivate() || event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfInfo()))) {
                if(event.getJDA().getGuildById(event.getInvite().getGuildId()) != null) {
                    try {
                        channel.sendMessage("Already in that Server!");
                    } catch (RuntimeException ignored) {} //no write perms or blocked pm
                    return;
                }
                if(!checkInviteAvailability()) {
                    channel.sendMessageAsync("I am currently not configured to accept invites!", null);
                } else {
                    channel.sendMessageAsync("I can no longer be invited via invite-links! " +
                            "Please use following link to invite me to your server (manage_server permission required): " +
                            "https://discordapp.com/oauth2/authorize?&client_id=" + PropertiesManager.load().getValue(Property.APP_ID) + "&scope=bot", null);
                }
            }
        }
    }
    
    private boolean checkInviteAvailability() {
		String temp = PropertiesManager.load().getValue(Property.ACCEPT_INVITES);
		return Boolean.parseBoolean(temp) || (Integer.parseInt(temp) == 0);
	}
}