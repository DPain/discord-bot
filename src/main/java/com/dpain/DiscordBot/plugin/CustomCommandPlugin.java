package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class CustomCommandPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(CustomCommandPlugin.class);

  public CustomCommandPlugin() {
    super("CustomCommandPlugin", Group.TRUSTED_USER);

    super.helpString = "**Custom Plugin Usage:** \n"
        + "-custom : Displays all the custom commands created by the users from the server.\n"
        + "-delcustom *\"userId\"* : Deletes a custom command.\n";
    EssentialsPlugin.appendHelpString(super.helpString);
  }

  @Override
  public void handleEvent(Event event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if (canAccessPlugin(castedEvent.getMember()) && !castedEvent.getAuthor().getId()
            .equals(castedEvent.getJDA().getSelfUser().getId())) {

          if (message.startsWith("!")) {
            /*
             * #TODO Implement
             */
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
