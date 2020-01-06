package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.ConsoleInputReader;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.listener.UserEventListener;
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

      pluginListener = new PluginListener(waiter, this);

      JDABuilder builder = new JDABuilder(AccountType.BOT)
          .setToken(PropertiesManager.load().getValue(Property.BOT_TOKEN));

      builder.addEventListeners(pluginListener);
      builder.addEventListeners(new UserEventListener());
      builder.addEventListeners(waiter);

      jda = builder.build().awaitReady();
      jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Bot Activated!"));

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
      jda.shutdown();
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
   * Quick method used to leave guild.
   */
  private void leaveGuild() {
    try {
      jda.getGuildById("244728414165139457").leave().complete();
    } catch(NullPointerException e) {
      System.out.println("Already left the server!");
    }
  }
}
