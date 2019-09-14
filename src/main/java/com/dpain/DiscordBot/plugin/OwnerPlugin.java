package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OwnerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(OwnerPlugin.class);

  public OwnerPlugin(EventWaiter waiter, DiscordBot bot) {
    super("OwnerPlugin", Group.OWNER, waiter, bot);
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
            if (message.startsWith("-username ")) {
              String param = message.substring(10);
              castedEvent.getJDA().getSelfUser().getManager().setName(param);
              castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param);

              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.startsWith("-group ")) {
              /**
               * #TODO Implement user change group feature. Also fix userdata.yml constant read
               * issue.
               */

              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-info")) {
              castedEvent.getChannel()
                  .sendMessage("**Server Info:**" + "\nName: " + castedEvent.getGuild().getName()
                      + "\nID: " + castedEvent.getGuild().getId() + "\nOwner Nickname: "
                      + castedEvent.getGuild().getOwner().getNickname() + "\nOwner ID: "
                      + castedEvent.getGuild().getOwner().getUser().getId())
                  .queue();

              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-rebuild")) {
              MemberManager.load().rebuild(castedEvent.getGuild());
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-update")) {
              MemberManager.load().update(castedEvent.getGuild());
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-reload")) {
              MemberManager.load().reload();
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-exit")) {
              MemberManager.load().saveConfig();
              G2gAlerter.load().saveConfig();
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              castedEvent.getJDA().shutdown();
              System.exit(0);
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
    super.commands.put("-username *\\\"name\\\"*", "Changes the username of the bot.");
    super.commands.put("-group *\\\"userId\\\" \\\"group\\\"*", "Changes the user's group.");
    super.commands.put("-info", "Displays the server info.");
    super.commands.put("-rebuild", "Resets the userdata file to default.");
    super.commands.put("-update", "Updates the userdata file.");
    super.commands.put("-reload", "Reads the userdata fil`e again.");
    super.commands.put("-exit", "Shutsdown the bot.");
  }

}
