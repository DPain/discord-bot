package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.Main;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.mcsplash.MinecraftSplashReader;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class EssentialsPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(EssentialsPlugin.class);

  private HashMap<String, File> emoteMap;
  private MinecraftSplashReader mcSplash;

  public EssentialsPlugin(EventWaiter waiter, DiscordBot bot) {
    super("EssentialsPlugin", Group.USER, waiter, bot);

    // Create img folder is it does not exist.
    Path imgDir = Paths.get("rsc/img");
    if (!Files.isDirectory(imgDir)) {
      try {
        Files.createDirectories(imgDir);
      } catch (IOException e) {
        // Fail to create directory
        e.printStackTrace();
      }
    }

    emoteMap = new HashMap<String, File>();
    instantiateEmoteMap();
    mcSplash = new MinecraftSplashReader(new File("rsc/splashes.txt"));
  }

  private void instantiateEmoteMap() {
    File imageFolder = new File("rsc/img");
    File[] listOfFiles = imageFolder.listFiles();

    // Puts all the emotes that are in the ./rsc/img directory into the HashMap
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        String fileName =
            listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf("."));
        String extension =
            listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf("."));
        String key = fileName.replace("(C)", ":");
        emoteMap.put(key.toLowerCase(), new File("./rsc/img/" + fileName + extension));
      }
    }
  }

  @Override
  public void handleEvent(Event event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if ((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            || canAccessPlugin(castedEvent.getMember())) {
          if (message.startsWith("-")) {
            if (message.equals("-splash")) {
              // Easter Egg
              castedEvent.getChannel().sendMessage(mcSplash.getRandomSplash()).queue();
              logger.info(LogHelper.elog(castedEvent, "User triggered the easter egg."));
            } else if (message.equals("-emotes")) {
              Set<String> set = emoteMap.keySet();
              String[] keys = set.toArray(new String[set.size()]);

              MessageHelper.sendPage("**Twitch Emotes: **", keys, 3, 50, waiter,
                  castedEvent.getChannel(), 1, TimeUnit.HOURS);
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-help")) {
              ArrayList<String> helpStrings = new ArrayList<String>();
              for (Plugin plugin : super.bot.pluginListener.plugins) {
                String pluginPage = String.format("**%s Plugin Usage**", plugin.getName());
                for (String cmd : plugin.getCommands().keySet()) {
                  pluginPage += String.format("\n%s : %s", cmd, plugin.getCommands().get(cmd));
                }
                helpStrings.add(pluginPage);
              }
              String[] commands = helpStrings.toArray(new String[helpStrings.size()]);

              MessageHelper.sendPage("**Help: **", commands, 3, 3, waiter,
                  castedEvent.getChannel(), 1, TimeUnit.HOURS);
              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            }
          }
        }
        // Also works for users who have lower permissions than this plugin's requirement.
        if (!castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
          // Checks if there are any emotes to display.
          for (String key : emoteMap.keySet()) {
            String effectiveMessage = message.toLowerCase();
            if (shouldDisplayEmote(key, effectiveMessage)) {
              castedEvent.getChannel()
                  .sendFile(emoteMap.get(key), String.format("%s.png", key), null).queue();
              logger.info(LogHelper.elog(castedEvent, String.format("Triggered emote: %s", key)));
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Determines whether the bot should display an emote.
   * 
   * @param key
   * @param msg
   * @return
   */
  private boolean shouldDisplayEmote(String key, String msg) {
    int start = msg.indexOf(key);
    int end = msg.indexOf(key) + key.length() - 1;
    return ((start > 0 && msg.charAt(start - 1) == ' ') || start == 0) && msg.contains(key)
        && (end == msg.length() - 1 || (end < msg.length() - 1 && msg.charAt(end + 1) == ' '));
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-splash", "Gets a random string.");
    super.commands.put("-emotes", "Deletes a custom command.");
    super.commands.put("-help", "Displays the available commands.");
  }
}
