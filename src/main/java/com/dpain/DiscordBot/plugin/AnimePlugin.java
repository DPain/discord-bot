package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.anime.AnimeTorrentFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class AnimePlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(AnimePlugin.class);

  private AnimeTorrentFinder animeTorrentFinder;

  public AnimePlugin(EventWaiter waiter, DiscordBot bot) {
    super("AnimePlugin", Group.USER, waiter, bot);
    animeTorrentFinder = new AnimeTorrentFinder();
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
        case "anime-search": {
          event.deferReply().queue(); // Let the user know we received the command before doing anything else
          
          String searchParam = event.getOption("name").getAsString();
          try {
            LinkedList<String> torrentInfo = animeTorrentFinder.searchTorrent(searchParam);
            for (String msg : torrentInfo) {
              event.getHook().sendMessage(msg).queue();
            }
          } catch (IOException e) {
            event.getHook().sendMessage(String.format("There were no torrent results for: %s", searchParam)).queue();
          }
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          
          break;
        }
        case "anime-today":
          event.deferReply().queue(); // Let the user know we received the command before doing anything else
          
          animeTorrentFinder.getCurrentSchedule();
          event.getHook().sendMessage("WIP").queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        case "anime-week":
          event.deferReply().queue(); // Let the user know we received the command before doing anything else
          
          animeTorrentFinder.getFullSchedule();
          event.getHook().sendMessage("WIP").queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands
        .add(new CommandData("anime-search", "Gets a list of torrrent from Tokyo toshokan.")
            .addOption(OptionType.STRING, "name", "Name of anime to search.", true));
    super.commands
        .add(new CommandData("anime-today", "Gets a list of animes airing today. Still WIP."));
    super.commands
        .add(new CommandData("anime-week", "Gets a list of animes airing this week. Still WIP."));
  }
}
