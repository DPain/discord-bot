package com.dpain.DiscordBot.plugin;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.system.MemberManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class Plugin {
  private final static Logger logger = LoggerFactory.getLogger(Plugin.class);

  public final DiscordBot bot;
  
  private final String name;
  private Group group;
  protected final EventWaiter waiter;
  protected HashMap<String, String> commands;

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
    
    this.commands = new HashMap<String, String>();
    this.bot = bot;
    
    setCommandDescriptions();

    logger.info(String.format("%s Plugin Initialized!", name));
  }

  public abstract void handleEvent(GenericEvent event);
  
  public abstract void setCommandDescriptions();

  /**
   * Puts a Command description into a HashMap.
   * @param command: syntax of the command.
   * @param desc: Description of the command.
   */
  public void putCommandDescription(String command, String desc) {
    commands.put(command, desc);
  }
  
  /**
   * Returns the Map of commands of that Plugin.
   * @return
   */
  public Map<String, String> getCommands() {
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
