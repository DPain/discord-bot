package com.dpain.DiscordBot.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.MemberManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;

public abstract class Plugin {
  private final static Logger logger = LoggerFactory.getLogger(Plugin.class);

  private final String name;
  private Group group;
  protected final EventWaiter waiter;
  protected String helpString;

  /**
   * Constructor
   * 
   * @param name Name of the plugin
   * @param group Default Group allowed to access the plugin
   */
  public Plugin(String name, Group group, EventWaiter waiter) {
    this.name = name;
    this.group = group;
    this.waiter = waiter;

    logger.info(String.format("%s Plugin Initialized!", name));
  }

  public abstract void handleEvent(Event event);

  public final String getHelpSpring() {
    return helpString;
  }

  public final String getName() {
    return name;
  }

  protected boolean canAccessPlugin(Member member) {
    return MemberManager.load().getMemberGroup(member).getHierarchy() <= group.getHierarchy();
  }
}
