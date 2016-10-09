package com.dpain.DiscordBot.listener.twitch;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public class TwitchAlerter {
	private Guild guild;
	
	public TwitchAlerter(Guild guild) {
		this.guild = guild;
	}
	
	public void notifyTwitchStream(User user) {
		guild.getPublicChannel().sendMessage(user.getUsername() + " started streaming in Twitch!");
	}
}
