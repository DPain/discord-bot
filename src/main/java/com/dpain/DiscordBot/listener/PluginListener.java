package com.dpain.DiscordBot.listener;

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
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
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class PluginListener implements EventListener {
  private final static Logger logger = LoggerFactory.getLogger(PluginListener.class);

  public LinkedList<Plugin> plugins;
  
  public DiscordBot bot;

  public PluginListener(EventWaiter waiter, DiscordBot bot) {
    // Maybe use one hashmap and have each plugins to add into the hashmap.
    plugins = new LinkedList<Plugin>();

    // Add EssentialsPlugin as the first plugin to use the -help command.
    plugins.add(new EssentialsPlugin(waiter, bot));

    plugins.add(new AnimePlugin(waiter, bot));
    plugins.add(new AudioPlayerPlugin(waiter, bot));
    // plugins.add(new CustomCommandPlugin(waiter, bot));
    plugins.add(new ModeratorPlugin(waiter, bot));
    plugins.add(new OwnerPlugin(waiter, bot));
    // plugins.add(new ProfanityGuardPlugin(waiter, bot));
    plugins.add(new SchedulerPlugin(waiter, bot));
    plugins.add(new WeatherPlugin(waiter, bot));
    plugins.add(new WikipediaPlugin(waiter, bot));
    if (PropertiesManager.load().getValue(Property.USE_GAME_ROLE).toUpperCase()
        .equals("TRUE")) {
      plugins.add(new GamerolePlugin(waiter, bot));
    }
    if (PropertiesManager.load().getValue(Property.USE_G2G_ALERTER).toUpperCase()
        .equals("TRUE")) {
      plugins.add(new G2gNotifierPlugin(waiter, bot));
    }

    logger.info("Added all the plugins!");
  }

  @Override
  public void onEvent(GenericEvent event) {
    for (Plugin plugin : plugins) {
      plugin.handleEvent(event);
    }
  }
}
