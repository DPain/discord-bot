package com.dpain.DiscordBot.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;
import com.dpain.DiscordBot.system.MemberManager;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;

public abstract class Plugin {
	private final static Logger logger = Logger.getLogger(Plugin.class.getName());
	
	private final String name;
	private Group group;
	protected String helpString;
	protected TextChannel loggingChannel;
	
	public Plugin(String name, Group group) {
		this(name, group, null);
	}
	
	public Plugin(String name, Group group, TextChannel loggingChannel) {
		this.name = name;
		this.group = group;
		this.loggingChannel = loggingChannel;
		logger.log(Level.INFO, "Initialized!");
	}
	
	public abstract void handleEvent(Event event);

	public final String getHelpSpring() {
		return helpString;
	}
	
	public final String getName() {
		return name;
	}
	
	public final void setLoggingChannel(TextChannel channel) {
		this.loggingChannel = channel;
	}
	
	public final TextChannel getLoggingChannel() {
		return this.loggingChannel;
	}
	
	protected boolean canAccessPlugin(Member member) {
		return MemberManager.load().getMemberGroup(member).getHierarchy() <= group.getHierarchy();
	}
}
