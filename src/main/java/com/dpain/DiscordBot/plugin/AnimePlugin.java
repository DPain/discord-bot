package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AnimePlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(AnimePlugin.class);

  private AnimeTorrentFinder animeTorrentFinder;

  public AnimePlugin(EventWaiter waiter, DiscordBot bot) {
    super("AnimePlugin", Group.USER, waiter, bot);
    animeTorrentFinder = new AnimeTorrentFinder();
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
            if (message.equals("-anime")) {
              // Incorrect usage of anime plugin.
              castedEvent.getChannel().sendMessage("*Try -help for correct syntax!*").queue();

              String temp =
                  LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message));
              logger.warn(temp);
            } else if (message.startsWith("-anime ")) {
              String param = message.substring(7);
              if (param.toLowerCase().startsWith("search ")) {
                String searchParam = param.substring(7);
                try {
                  LinkedList<String> torrentInfo = animeTorrentFinder.searchTorrent(searchParam);
                  for (String msg : torrentInfo) {
                    castedEvent.getChannel().sendMessage(msg).queue();
                  }
                } catch (IOException e) {
                  castedEvent.getChannel()
                      .sendMessage("There were no torrent results for: " + searchParam).queue();
                }
              } else if (param.equals("today")) {
                animeTorrentFinder.getCurrentSchedule();
                castedEvent.getChannel().sendMessage("WIP").queue();
              } else if (param.equals("week")) {
                animeTorrentFinder.getFullSchedule();
                castedEvent.getChannel().sendMessage("WIP").queue();
              }

              String temp = LogHelper.elog(castedEvent, String.format("Command: %s", message));
              logger.info(temp);
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
    super.commands.put("-anime search *\\\"name\\\"*",
        "Gets a list of torrrent from Tokyo toshokan.");
    super.commands.put("-anime today/week", "Gets the anime schedule");
  }

}
