package com.dpain.DiscordBot.plugin;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.MemberManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class Plugin extends ListenerAdapter {
  private final static Logger logger = LoggerFactory.getLogger(Plugin.class);

  public final DiscordBot bot;
  
  private final String name;
  private Group group;
  protected final EventWaiter waiter;
  protected List<CommandData> commands;

  /**
   * Constructor
   * 
   * @param name Name of the plugin
   * @param group Default Group allowed to access the plugin
   */
  public Plugin(String name, Group group, EventWaiter waiter, DiscordBot bot) {
    this.name = name;
    this.group = group;
    this.waiter = waiter;
    
    this.commands = new ArrayList<CommandData>();
    this.bot = bot;
    
    setCommandDescriptions();

    logger.info(String.format("%s Plugin Initialized!", name));
  }
  
  public abstract void setCommandDescriptions();

  
  /**
   * Returns the List of commands of the Plugin.
   * @return
   */
  public List<CommandData> getCommands() {
    return commands;
  }

  /**
   * Gets the name of the Plugin.
   * @return
   */
  public final String getName() {
    return name;
  }

  protected boolean canAccessPlugin(Member member) {
    return MemberManager.load().getMemberGroup(member).getHierarchy() <= group.getHierarchy();
  }
}
