package com.dpain.DiscordBot.plugin.moderator;

import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

public class Cleaner implements Runnable {
  private final static Logger logger = LoggerFactory.getLogger(Cleaner.class);

  private TextChannel targetChannel;
  private List<Message> messages;
  private int count;
  
  private boolean successful;

  /**
   * Constructor
   * @param targetChannel channel to delete messages.
   * @param i number of messages
   * @throws RateLimitedException
   */
  public Cleaner(TextChannel targetChannel, int i) throws RateLimitedException {
    logger.info(String.format("Cleaner Initialized!"));

    this.targetChannel = targetChannel;

    // Plugin shouldn't allow this case, but just for safety.
    if (i < 0) {
      i = 0;
    }

    // Need one extra message to exclude the clear request message.
    messages = targetChannel.getHistory().retrievePast(i + 1).complete(true);
    count = i;
    
    successful = true;
  }

  /**
   * Cleaner Thread task
   */
  @Override
  public void run() {
    String text = String.format("Deleting %d messages.", count);
    logger.info(text);

    // Does not delete the clear request message.
    messages.remove(0);

    Iterator<Message> iter = messages.iterator();
    while (iter.hasNext()) {
      Message msg = iter.next();
      try {
        msg.delete().queue();
      } catch(InsufficientPermissionException e) {
        logger.error("Bot lacks Permissions to delete some of the messages!");
        successful = false;
      }
    }

    if(!successful) {
      text += "\nThe bot lacks Permissions so it only deleted the messages it could delete!";
    }
    
    targetChannel.sendMessage(text).queue();

    logger.info("Message deletion all queued. Thread now terminated!");
    return;
  }
}
