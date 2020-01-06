package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.net.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.wiki.WikiFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WikipediaPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(WikipediaPlugin.class);

  public WikipediaPlugin(EventWaiter waiter, DiscordBot bot) {
    super("WikipediaPlugin", Group.USER, waiter, bot);
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

            if (message.equals("-wiki")) {
              castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();
            } else if (message.startsWith("-wiki ")) {
              String searchParam = message.substring(6);
              try {
                castedEvent.getChannel()
                    .sendMessage(String.format("**Wiki Search of** ***%s*** **: **\n%s",
                        URLDecoder.decode(searchParam, "UTF-8"), WikiFinder.search(searchParam)))
                    .queue();
              } catch (IOException e) {
                castedEvent.getChannel()
                    .sendMessage("There were no Wiki results for: " + searchParam).queue();
              }
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
    super.commands.put("-wiki *\\\"searchParam\\\"*", "Searches the a topic in Wikipedia.");
  }
}
