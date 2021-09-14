package com.dpain.DiscordBot.plugin;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Timezone;
import com.dpain.DiscordBot.helper.LogHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SchedulerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(SchedulerPlugin.class);

  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

  public SchedulerPlugin(EventWaiter waiter, DiscordBot bot) {
    super("SchedulerPlugin", Group.USER, waiter, bot);
  }

  public static int hoursToSeconds(long time) {
    int hours = (int) time;
    int minutes = (int) (time * 60) % 60;
    int seconds = (int) (time * (60 * 60)) % 60;

    int result = (hours * 3600) + (minutes * 60) + seconds;

    return result;
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
        case "remind": {
          event.deferReply().queue();

          try {
            long hours = event.getOption("hours-later").getAsLong();
            String description = event.getOption("description").getAsString();

            event.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage(description)
                .queueAfter(SchedulerPlugin.hoursToSeconds(hours), TimeUnit.SECONDS));

            event.getHook().sendMessage(String.format("Reminder set %.4f hours later for: %s", hours, description))
                .queue();

            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } catch (NumberFormatException e) {
            event.getHook().sendMessage("Please input a correct time in hours!").queue();

            logger.warn(LogHelper.elog(event, String.format("Incorrect command: %s", message)));
          }

          break;
        }
        case "time": {
          OptionMapping option = event.getOption("hours-later");
          try {
            String result = "";

            ZonedDateTime time = ZonedDateTime.now();

            if (option != null) {
              long hours = option.getAsLong();
              time = time.plusHours(hours);
            }

            Timezone[] timezones = Timezone.class.getEnumConstants();

            // Sorting preset timezones because it looks better.
            Arrays.sort(timezones, (a, b) -> {
              LocalDateTime instant = LocalDateTime.now();
              ZoneOffset aOffset = a.getZoneId().getRules().getOffset(instant);
              ZoneOffset bOffset = b.getZoneId().getRules().getOffset(instant);
              return bOffset.compareTo(aOffset);
            });

            for (Timezone zone : timezones) {
              result += String.format("\n%s %s",
                  time.withZoneSameInstant(zone.getZoneId()).format(formatter),
                  zone.getZoneId().toString());
            }

            event.reply("Time for some timezones: " + result).queue();
            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } catch (NumberFormatException e) {
            event.reply("Please input a correct time in hours!").queue();
            logger.warn(LogHelper.elog(event, String.format("Incorrect command: %s", message)));
          }

          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("remind", "Sets a reminder.")
        .addOption(OptionType.INTEGER, "hours-later", "Number of hours later to be reminded", true)
        .addOption(OptionType.STRING, "description", "Description of the Reminder", true));
    super.commands.add(new CommandData("time", "Gets the current time for a set of Timezones.")
        .addOption(OptionType.INTEGER, "hours-later", "Number of hours to add.", false));
  }
}
