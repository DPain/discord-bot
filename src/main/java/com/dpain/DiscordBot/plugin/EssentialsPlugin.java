package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.mcsplash.MinecraftSplashReader;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class EssentialsPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(EssentialsPlugin.class);

  private HashMap<String, File> emoteMap;
  private MinecraftSplashReader mcSplash;
  private String twitchEmoteList;

  private static String helpString = "";

  public static void appendHelpString(String help) {
    helpString += help;
  }

  public EssentialsPlugin() {
    super("EssentialsPlugin", Group.USER);

    emoteMap = new HashMap<String, File>();
    instantiateEmoteMap();
    mcSplash = new MinecraftSplashReader("./rsc/splashes.txt");

    super.helpString = "**Essentials Plugin Usage:** \n" + "-splash : Gets a random string.\n"
        + "-emotes : Returns the list of twitch emotes available.\n"
        + "-help : Displays the available commands.\n";

    appendHelpString(super.helpString);
  }

  private void instantiateEmoteMap() {
    File imageFolder = new File("./rsc/img");
    File[] listOfFiles = imageFolder.listFiles();

    String temp = "";

    // Puts all the emotes that are in the ./rsc/img directory into the HashMap
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        String fileName =
            listOfFiles[i].getName().substring(0, listOfFiles[i].getName().lastIndexOf("."));
        String extension =
            listOfFiles[i].getName().substring(listOfFiles[i].getName().lastIndexOf("."));
        String key = fileName.replace("(C)", ":");
        emoteMap.put(key.toLowerCase(), new File("./rsc/img/" + fileName + extension));
        temp += key + ", ";
      }
    }
    temp = temp.substring(0, temp.length() - 2);
    twitchEmoteList = temp;
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
              /**
               * @TODO Refine message format to not have commands to be split.
               */
              if (twitchEmoteList.length() > 1800) {
                int numRecurssion = twitchEmoteList.length() / 1800;
                // Safe margin of 1800 instead of 2000
                for (int i = 0; i <= numRecurssion; i++) {
                  if (i == 0) {
                    String temp = twitchEmoteList.substring(i * 1800, (1 + i) * 1800);
                    castedEvent.getChannel().sendMessage("**Twitch Emotes:** \n*" + temp + "*\n ("
                        + (i + 1) + "/" + (numRecurssion + 1) + ")");
                  } else if (i >= numRecurssion) {
                    String temp = twitchEmoteList.substring(i * 1800);
                    castedEvent.getChannel().sendMessage(
                        "*" + temp + "*\n (" + (i + 1) + "/" + (numRecurssion + 1) + ")");
                  } else {
                    String temp = twitchEmoteList.substring(i * 1800, (1 + i) * 1800);
                    castedEvent.getChannel().sendMessage(
                        "*" + temp + "*\n (" + (i + 1) + "/" + (numRecurssion + 1) + ")");
                  }
                }
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } else {
                castedEvent.getChannel()
                    .sendMessage("**Twitch Emotes:** \n*" + twitchEmoteList + "*").queue();
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              }
            } else if (message.equals("-help")) {
              castedEvent.getChannel().sendMessage(EssentialsPlugin.helpString).queue();
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
}
