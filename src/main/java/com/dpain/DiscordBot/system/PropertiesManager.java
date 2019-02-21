package com.dpain.DiscordBot.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.listener.PluginListener;
import com.dpain.DiscordBot.plugin.AudioPlayerPlugin;

public class PropertiesManager {
  private final static Logger logger = LoggerFactory.getLogger(PropertiesManager.class);

  private static PropertiesManager ref;

  private Properties settings;

  private PropertiesManager() {
    File configFile = new File("bot.properties");
    try {
      if (!configFile.exists()) {
        logger.warn(String.format("%s file does not exist! Creating a default file.",
            configFile.getName()));
        setupPropertiesFile();
      }

      settings = new Properties();
      settings.load(new FileReader(configFile));
    } catch (FileNotFoundException e) {
      logger.error(
          String.format("%s file does not exist and could not be created!", configFile.getName()));
      System.exit(1);
    } catch (IOException e) {
      logger.error(String.format("%s file cannot be read!", configFile.getName()));
      System.exit(1);
    }
  }

  public static PropertiesManager loadPropertiesManager() {
    if (ref == null) {
      ref = new PropertiesManager();
    }
    return ref;
  }

  /**
   * Just a shorter version for loadPropertiesManager().
   * 
   * @return a singleton of PropertiesManager
   */
  public static PropertiesManager load() {
    return loadPropertiesManager();
  }

  public String getValue(Property property) {
    return settings.getProperty(property.getKey());
  }

  private void setupPropertiesFile() throws IOException {
    LocalDateTime timePoint = LocalDateTime.now();

    File configFile = new File("bot.properties");
    configFile.createNewFile();

    String line = String.format(
        "#Properties file created at: %s\n%s=\n%s=\n%s=\n%s=\n%s=\n%s=\n%s=\n%s=false\n%s=false\n%s=false\n%s=\n%s=5",
        timePoint.toString(), Property.USERNAME.getKey(), Property.BOT_ID.getKey(),
        Property.BOT_TOKEN.getKey(), Property.APP_ID.getKey(), Property.OWNER_USER_ID.getKey(),
        Property.GUILD_ID.getKey(), Property.WEATHER_API_KEY.getKey(),
        Property.GAME_ROLE_FEATURE.getKey(), Property.USE_TWITCH_ALERTER.getKey(),
        Property.GREET_GUILD_MEMBER.getKey(), Property.LOGGER_CHANNEL_ID.getKey(),
        Property.TORRENT_ENTRY_LIMIT.getKey());

    PrintStream printer = new PrintStream(configFile);
    printer.print(line);
    printer.close();
  }
}
