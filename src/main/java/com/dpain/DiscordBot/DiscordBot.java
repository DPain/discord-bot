package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.security.auth.login.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.Plugin;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.dpain.DiscordBot.system.MemberManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordBot {
  private final static Logger logger = LoggerFactory.getLogger(DiscordBot.class);
  private JDA jda;

  public PluginListener pluginListener;

  public DiscordBot() {

    try {
      // Defines an EventWaiter used for paginators.
      EventWaiter waiter = new EventWaiter();

      // Initialized before PluginListener since a plugin might rely on some
      // properties.
      PropertiesManager.load();

      String token = PropertiesManager.load().getValue(Property.BOT_TOKEN);

      JDABuilder builder =
          JDABuilder.createDefault(token).enableIntents(EnumSet.noneOf(GatewayIntent.class));

      // Registering event listeners.
      builder.addEventListeners(new UserEventListener(this));
      builder.addEventListeners(waiter);

      pluginListener = new PluginListener(waiter, this);
      /**
       * Adding plugins to event listener so that it can respond to requests.
       */
      for (Plugin plugins : pluginListener.getPlugins()) {
        builder.addEventListeners(plugins);
      }

      // Finalize building the bot.
      jda = builder.build().awaitReady();

      /**
       * Register commands for all the plugins added to Discord. Needs to be done after bot.jda is
       * built.
       */
      pluginListener.registerCommands();

      MemberManager
          .setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));
      MemberManager.load();
      UserEventListener
          .setDefaultGuild(jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID)));

      G2gAlerter.load().setJDA(jda);

      logger.info("Registered Guilds:");
      for (Guild guild : jda.getGuilds()) {
        logger.info(String.format("Name: %s id: %s: ", guild.getName(), guild.getId()));
      }
      logger.info("Bot is running!");
    } catch (LoginException e) {
      logger.error("The provided Login information is incorrect. Please provide valid details.");
      System.exit(0);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      jda.shutdownNow();
      System.exit(0);
    }
    // changeAvatar();
    // leaveGuild();
  }

  public void readConsole() {
    (new Thread(new ConsoleInputReader(jda, pluginListener,
        jda.getGuildById(PropertiesManager.load().getValue(Property.GUILD_ID))))).start();
  }

  /**
   * Quick method used to change profile picture.
   */
  private void changeAvatar() {
    Icon icon = null;
    try {
      icon = Icon.from(new File("File Path"));
    } catch (IOException e) {
      logger.error("The image file does not exist!");
    }
    jda.getSelfUser().getManager().setAvatar(icon).complete();
  }
  
  /**
   * Quick method to clear global commands
   */
  public void clearCommands() {
    // These commands take up to an hour to be activated after creation/update/delete
    CommandListUpdateAction commands = jda.updateCommands();

    commands.addCommands(new ArrayList<CommandData>()).queue();
  }

  /**
   * Quick method used to leave guild.
   */
  private void leaveGuild() {
    try {
      jda.getGuildById("244728414165139457").leave().complete();
    } catch (NullPointerException e) {
      System.out.println("Already left the server!");
    }
  }

  /**
   * Gets the JDA instance.
   * 
   * @return JDA
   */
  public JDA getJDA() {
    return jda;
  }
}
