package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.weather.WeatherDataSet;
import com.dpain.DiscordBot.plugin.weather.WeatherFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class WeatherPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(WeatherPlugin.class);

  public WeatherPlugin(EventWaiter waiter, DiscordBot bot) {
    super("WeatherPlugin", Group.USER, waiter, bot);
  }
  
  @Override
  public void onSlashCommand(SlashCommandEvent event) {
    if (canAccessPlugin(event.getMember())
        && !event.getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
      String message = String.format("CMD: %s - %s", event.getName(), Arrays.toString(event.getOptions().toArray()));

      // Only accept commands from guilds.
      if (event.getGuild() == null) {
        return;
      }
      switch (event.getName()) {
        case "weather": {
          event.deferReply().queue();
          
          String param = event.getOption("name").getAsString();

          // Gets the weather data every 3 hour.

          WeatherFinder weatherFinder = new WeatherFinder();
          WeatherDataSet weatherDataSet = weatherFinder.getWeathersByCity(param);

          String msg = "***" + weatherDataSet.getCity() + "'s*** **Weather Forecast:**";

          for (int i = 0; i < weatherDataSet.getDataSet().size() && i < 5; i++) {
            msg += weatherDataSet.getDataSet().get(i).getCommonDataToString();
          }
          event.getHook().sendMessage(msg).queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));

          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("weather", "Gets the weather at a location.")
        .addOption(OptionType.STRING, "name", "Name of the location", true));
  }
}
