package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ProfanityGuardPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(ProfanityGuardPlugin.class);

  private boolean activated = false;

  /**
   * #TODO In future, implement strike out system and save into a file
   */

  public ProfanityGuardPlugin(EventWaiter waiter, DiscordBot bot) {
    super("ProfanityGuardPlugin", Group.MODERATOR, waiter, bot);
  }

  @Override
  public void handleEvent(Event event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if ((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            || canAccessPlugin(castedEvent.getMember())) {
          if (message.startsWith("-")) {
            if (message.equals("-profguard")) {
              // Incorrect usage of profguard
              castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();
              logger.warn(
                  LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
            } else if (message.startsWith("-profguard ")) {
              String param = message.substring(11);
              if (param.toLowerCase().equals("enable")) {
                activated = true;
                castedEvent.getChannel().sendMessage("*Profanity Guard is ENABLED!*");
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } else if (param.equals("disable")) {
                activated = false;
                castedEvent.getChannel().sendMessage("*Profanity Guard is DISABLED!*");
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } else if (param.equals("offenders")) {
                castedEvent.getAuthor().openPrivateChannel().complete().sendMessage("WIP");
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } else {
                // Incorrect usage of profguard
                castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*");
                logger.warn(
                    LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
              }
            }
          }
          /*
           * # TODO Implement
           * if() {
           * 
           * }
           */
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-profguard *\\\"enable/disable\\\"*", "Enables or disables Profanity Guard.");
    super.commands.put("-profguard *\\\"offenders\\\"*", "PMs the list of offenders to the one who issued the command.");
  }

}
