package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.wiki.WikiFinder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class WikipediaPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(WikipediaPlugin.class);

  public WikipediaPlugin(EventWaiter waiter, DiscordBot bot) {
    super("WikipediaPlugin", Group.USER, waiter, bot);
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
        case "wiki": {
          event.deferReply().queue();
          
          String param = event.getOption("search-param").getAsString();

          try {
            event.reply(String.format("**Wiki Search of** ***%s*** **: **\n%s",
                    URLDecoder.decode(param, "UTF-8"), WikiFinder.search(param)))
                .queue();
          } catch (IOException e) {
            event.getHook().sendMessage("There were no Wiki results for: " + param).queue();
          }
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));

          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("wiki", "Searches a topic in Wikipedia.")
        .addOption(OptionType.STRING, "search-param", "Topic to search on Wikipedia", true));
  }
}
