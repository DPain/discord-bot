package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class CustomCommandPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(CustomCommandPlugin.class);

  public CustomCommandPlugin(EventWaiter waiter, DiscordBot bot) {
    super("CustomCommandPlugin", Group.TRUSTED_USER, waiter, bot);
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

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-custom",
        "Displays all the custom commands created by the users from the server.");
    super.commands.put("-delcustom *\\\"userId\\\"*", "Deletes a custom command.");
  }

}
