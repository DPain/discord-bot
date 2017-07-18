package com.dpain.DiscordBot.listener;

import java.util.LinkedList;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.plugin.AnimePlugin;
import com.dpain.DiscordBot.plugin.AudioPlayerPlugin;
import com.dpain.DiscordBot.plugin.Plugin;
import com.dpain.DiscordBot.plugin.CustomCommandPlugin;
import com.dpain.DiscordBot.plugin.EssentialsPlugin;
import com.dpain.DiscordBot.plugin.GameRolePlugin;
import com.dpain.DiscordBot.plugin.ModeratorPlugin;
import com.dpain.DiscordBot.plugin.OwnerPlugin;
import com.dpain.DiscordBot.plugin.ProfanityGuardPlugin;
import com.dpain.DiscordBot.plugin.SchedulerPlugin;
import com.dpain.DiscordBot.plugin.WeatherPlugin;
import com.dpain.DiscordBot.plugin.WikipediaPlugin;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;
import com.dpain.DiscordBot.system.PropertiesManager;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.hooks.EventListener;

public class PluginListener implements EventListener {
	private String name;
	private LinkedList<Plugin> plugins;
	
	public PluginListener(JDA jda) {
		name = "PluginListener";
		
		//Maybe use one hashmap and have each plugins to add into the hashmap. 
		plugins = new LinkedList<Plugin>();
		
		plugins.add(new AnimePlugin());
		plugins.add(new AudioPlayerPlugin());
		//plugins.add(new CustomCommandPlugin());
		plugins.add(new EssentialsPlugin());
		plugins.add(new ModeratorPlugin());
		plugins.add(new OwnerPlugin());
		//plugins.add(new ProfanityGuardPlugin());
		plugins.add(new SchedulerPlugin());
		plugins.add(new WeatherPlugin());
		plugins.add(new WikipediaPlugin());
		if(PropertiesManager.load().getValue(Property.GAME_ROLE_FEATURE).toUpperCase().equals("TRUE")) {
			plugins.add(new GameRolePlugin());
		}
		
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln(name, "Listening started!"));
	}
	
	public void onEvent(Event event) {
		for(Plugin plugin : plugins) {
        	plugin.handleEvent(event);
        }
	}
}
