package com.dpain.DiscordBot.listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class PluginListener {
  private final static Logger logger = LoggerFactory.getLogger(PluginListener.class);

  private List<Plugin> plugins;

  private DiscordBot bot;

  public PluginListener(EventWaiter waiter, DiscordBot bot) {
    this.bot = bot;

    plugins = new ArrayList<Plugin>();

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
    if (PropertiesManager.load().getValue(Property.USE_GAME_ROLE).toUpperCase().equals("TRUE")) {
      plugins.add(new GamerolePlugin(waiter, bot));
    }
    String g2gInterval = PropertiesManager.load().getValue(Property.G2G_ALERT_INTERVAL);
    if (g2gInterval != null && !g2gInterval.isEmpty()) {
      // String value is checked within the plugin.
      plugins.add(new G2gNotifierPlugin(waiter, bot));
    }

    logger.info("Added all the plugins!");
  }

  public List<Plugin> getPlugins() {
    return plugins;
  }

  public void registerCommands() {
    for (Guild guild : bot.getJDA().getGuilds()) {
      // These commands take up to an hour to be activated after creation/update/delete
      CommandListUpdateAction commands = guild.updateCommands();

      for (Plugin plugin : plugins) {
        commands.addCommands(plugin.getCommands());
      }

      commands.queue();
    }
  }
}
