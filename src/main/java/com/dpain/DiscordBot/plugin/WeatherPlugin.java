package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.weather.WeatherDataSet;
import com.dpain.DiscordBot.plugin.weather.WeatherFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WeatherPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(WeatherPlugin.class);

  public WeatherPlugin(EventWaiter waiter, DiscordBot bot) {
    super("WeatherPlugin", Group.USER, waiter, bot);
  }

  @Override
  public void handleEvent(GenericEvent event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if ((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            || canAccessPlugin(castedEvent.getMember())) {
          if (message.startsWith("-")) {
            if (message.startsWith("-weather ")) {
              String param = message.substring(9);

              // Gets the weather data every 3 hour.

              WeatherFinder weatherFinder = new WeatherFinder();
              WeatherDataSet weatherDataSet = weatherFinder.getWeathersByCity(param);

              String msg = "***" + weatherDataSet.getCity() + "'s*** **Weather Forecast:**";

              for (int i = 0; i < weatherDataSet.getDataSet().size() && i < 5; i++) {
                msg += weatherDataSet.getDataSet().get(i).getCommonDataToString();
              }
              castedEvent.getChannel().sendMessage(msg).queue();
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-weather *\\\"name\\\"*", "Gets the weather at a location.");
  }
}
