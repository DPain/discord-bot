package com.dpain.DiscordBot.plugin.moderator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.dpain.DiscordBot.helper.LogHelper;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Cleaner implements Runnable {
  private final static Logger logger = Logger.getLogger(Cleaner.class.getName());

  private MessageHistory messageHistory;
  private List<Message> messages;
  private int count;

  public Cleaner(TextChannel targetChannel, int i) throws RateLimitedException {
    logger.log(Level.INFO, String.format("Cleaner Initialized!"));

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
          logger.log(Level.INFO, String.format("Deleted %d messages.", count - 1));
          return;
        }

        item.delete().queue();
        i++;
      }
      if (messages == null) {
        logger.log(Level.INFO, "No more messages left.");
        return;
      }
    }
  }
}
