package com.dpain.DiscordBot.plugin;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Timezone;
import com.dpain.DiscordBot.helper.LogHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class SchedulerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(SchedulerPlugin.class);

  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

  public SchedulerPlugin(EventWaiter waiter, DiscordBot bot) {
    super("SchedulerPlugin", Group.TRUSTED_USER, waiter, bot);
  }

  @Override
  public void handleEvent(Event event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if (canAccessPlugin(castedEvent.getMember()) && !castedEvent.getAuthor().getId()
            .equals(castedEvent.getJDA().getSelfUser().getId())) {
          if (message.startsWith("-")) {
            if (message.startsWith("-remind ")) {
              String param = message.substring(8);
              try {
                int indexOfFirstSpace = param.indexOf(" ");
                double hours = Double.parseDouble(param.substring(0, indexOfFirstSpace));
                String description = param.substring(indexOfFirstSpace + 1);

                castedEvent.getAuthor().openPrivateChannel()
                    .queue((channel) -> channel.sendMessage(description)
                        .queueAfter(SchedulerPlugin.hoursToSeconds(hours), TimeUnit.SECONDS));

                castedEvent.getChannel()
                    .sendMessage(
                        String.format("Reminder set %.4f hours later for: %s", hours, description))
                    .queue();

                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } catch (NumberFormatException e) {
                castedEvent.getChannel().sendMessage("Please input a correct time in hours!")
                    .queue();

                logger.warn(
                    LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
              }
            } else if (message.startsWith("-time ")) {
              String param = message.substring(6);
              try {
                long hours = Long.parseLong(param.substring(0));

                String result = "";

                ZonedDateTime time = ZonedDateTime.now();
                time.plusHours(hours);

                for (Timezone zone : Timezone.class.getEnumConstants()) {
                  result += String.format("\n%s %s",
                      time.withZoneSameInstant(zone.getZoneId()).format(formatter),
                      zone.getZoneId().toString());
                }

                castedEvent.getChannel().sendMessage("Time for some timezones: " + result).queue();
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } catch (NumberFormatException e) {
                castedEvent.getChannel().sendMessage("Please input a correct time in hours!")
                    .queue();
                logger.warn(
                    LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static int hoursToSeconds(double time) {
    int hours = (int) time;
    int minutes = (int) (time * 60) % 60;
    int seconds = (int) (time * (60 * 60)) % 60;

    int result = (hours * 3600) + (minutes * 60) + seconds;

    return result;
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-remind *\\\"hours later\\\"* *\\\"description\\\"*", "Sets a reminder for x hours later.");
    super.commands.put("-time*\\\"hours later\\\"*", "Gets the time x hours later for a set of Timezones.");
  }
}
