package com.dpain.DiscordBot.plugin.reminder;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.plugin.AudioPlayerPlugin;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Reminder extends TimerTask {
  private final static Logger logger = LoggerFactory.getLogger(Reminder.class);

  private User who;
  private TextChannel channel;
  private String description;

  /**
   * Constructor
   * 
   * @param channel
   * @param description
   */
  public Reminder(User who, TextChannel channel, String description) {
    this.who = who;
    this.channel = channel;
    this.description = description;
  }

  @Override
  public void run() {
    channel.sendMessage(String.format("%s Reminder: %s",
        channel.getGuild().getMember(who).getEffectiveName(), description)).queue();
    logger.info(String.format(
        "Reminded member: %s (username: %s) at channel: %s in guild: %s\nDescription: %s",
        channel.getGuild().getMember(who).getEffectiveName(), who.getName(), channel.getName(),
        channel.getGuild().getName(), description));
  }
}
