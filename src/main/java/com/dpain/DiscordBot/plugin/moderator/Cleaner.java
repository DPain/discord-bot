package com.dpain.DiscordBot.plugin.moderator;

import java.util.Iterator;
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

  private TextChannel targetChannel;
  private List<Message> messages;
  private int count;

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
  }

  @Override
  public void run() {
    String text = String.format("Deleting %d messages.", count);
    logger.info(text);

    // Does not delete the clear request message.
    messages.remove(0);

    Iterator<Message> iter = messages.iterator();
    while (iter.hasNext()) {
      Message msg = iter.next();
      msg.delete().queue();
    }

    targetChannel.sendMessage(text).queue();

    logger.info("Message deletion all queued. Thread now terminated!");
    return;
  }
}
