package com.dpain.DiscordBot.listener;

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.plugin.AnimePlugin;
import com.dpain.DiscordBot.plugin.AudioPlayerPlugin;
import com.dpain.DiscordBot.plugin.Plugin;
import com.dpain.DiscordBot.plugin.EssentialsPlugin;
import com.dpain.DiscordBot.plugin.G2gNotifierPlugin;
import com.dpain.DiscordBot.plugin.GamerolePlugin;
import com.dpain.DiscordBot.plugin.ModeratorPlugin;
import com.dpain.DiscordBot.plugin.OwnerPlugin;
import com.dpain.DiscordBot.plugin.SchedulerPlugin;
import com.dpain.DiscordBot.plugin.WeatherPlugin;
import com.dpain.DiscordBot.plugin.WikipediaPlugin;
import com.dpain.DiscordBot.system.PropertiesManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public class PluginListener implements EventListener {
  private final static Logger logger = LoggerFactory.getLogger(PluginListener.class);

  private LinkedList<Plugin> plugins;

  public PluginListener(JDA jda) {
    // Maybe use one hashmap and have each plugins to add into the hashmap.
    plugins = new LinkedList<Plugin>();

    // Add EssentialsPlugin as the first plugin to use the -help command.
    plugins.add(new EssentialsPlugin());

    plugins.add(new AnimePlugin());
    plugins.add(new AudioPlayerPlugin());
    // plugins.add(new CustomCommandPlugin());
    plugins.add(new ModeratorPlugin());
    plugins.add(new OwnerPlugin());
    // plugins.add(new ProfanityGuardPlugin());
    plugins.add(new SchedulerPlugin());
    plugins.add(new WeatherPlugin());
    plugins.add(new WikipediaPlugin());
    if (PropertiesManager.load().getValue(Property.GAME_ROLE_FEATURE).toUpperCase()
        .equals("TRUE")) {
      plugins.add(new GamerolePlugin());
    }
    if (PropertiesManager.load().getValue(Property.USE_G2G_ALERTER).toUpperCase()
        .equals("TRUE")) {
      plugins.add(new G2gNotifierPlugin());
    }

    logger.info("Added all the plugins!");
  }

  public void onEvent(Event event) {
    for (Plugin plugin : plugins) {
      plugin.handleEvent(event);
    }
  }
}
