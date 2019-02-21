package com.dpain.DiscordBot.plugin.moderator;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.AudioPlayerPlugin;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Cleaner implements Runnable {
  private final static Logger logger = LoggerFactory.getLogger(Cleaner.class);

  private MessageHistory messageHistory;
  private List<Message> messages;
  private int count;

  public Cleaner(TextChannel targetChannel, int i) throws RateLimitedException {
    logger.info(String.format("Cleaner Initialized!"));

    messageHistory = targetChannel.getHistory();
    messages = messageHistory.retrievePast(i).complete(true);

    count = i + 1;
  }

  @Override
  public void run() {

    int i = 0;
    while (messages != null && i < count) {
      for (Message item : messages) {

        if (i >= count) {
          logger.info(String.format("Deleted %d messages.", count - 1));
          return;
        }

        item.delete().queue();
        i++;
      }
      if (messages == null) {
        logger.info("No more messages left.");
        return;
      }
    }
  }
}
