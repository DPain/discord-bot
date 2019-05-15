package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.g2g.G2gAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OwnerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(OwnerPlugin.class);

  public OwnerPlugin() {
    super("OwnerPlugin", Group.OWNER);

    super.helpString =
        "**Owner Plugin Usage:** \n" + "-username *\"name\"* : Changes the username of the bot.\n"
            + "-group *\"userId\" \"group\"* : Changes the user's group.\n"
            + "-info : Displays the server info.\n"
            + "-rebuild : Resets the userdata file to default.\n"
            + "-update : Updates the userdata file.\n"
            + "-reload : Reads the userdata file again.\n" + "-exit : Shutsdown the bot.\n";
    EssentialsPlugin.appendHelpString(super.helpString);
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

}
