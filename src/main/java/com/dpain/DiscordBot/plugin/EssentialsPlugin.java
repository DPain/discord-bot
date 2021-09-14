package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.mcsplash.MinecraftSplashReader;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.AttachmentOption;

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
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    try {
      String message = event.getMessage().getContentRaw();

      // Also works for users who have lower permissions than this plugin's requirement.
      if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
        // Checks if there are any emotes to display.
        for (String key : emoteMap.keySet()) {
          String effectiveMessage = message.toLowerCase();
          if (shouldDisplayEmote(key, effectiveMessage)) {
            // No attachment options set. (Attachment Options are things like spoiler alerts)
            AttachmentOption[] options = new AttachmentOption[0];
            event.getChannel().sendFile(emoteMap.get(key), options).queue();
            logger.info(LogHelper.elog(event, String.format("Triggered emote: %s", key)));
          }
        }
      }
    } catch (Exception e) {
      logger.error("Error happened while hanlding EssentialsPlugin Emotes.");
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
  public void onSlashCommand(SlashCommandEvent event) {
    if (canAccessPlugin(event.getMember())
        && !event.getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
      String message = String.format("CMD: %s - %s", event.getName(), Arrays.toString(event.getOptions().toArray()));

      // Only accept commands from guilds.
      if (event.getGuild() == null) {
        return;
      }
      switch (event.getName()) {
        case "splash":
          // Easter Egg
          event.reply(mcSplash.getRandomSplash()).queue();

          logger.info(LogHelper.elog(event, "User triggered the easter egg."));
          break;
        case "emotes":
          event.deferReply().queue();
          
          Set<String> set = emoteMap.keySet();
          String[] keys = set.toArray(new String[set.size()]);

          MessageHelper.sendPage("**Twitch Emotes: **", keys, 3, 50, waiter, event.getTextChannel(),
              1, TimeUnit.HOURS);
          
          event.getHook().sendMessage("Command Processed!").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("splash", "Gets a random splash text from Minecraft."));
    super.commands.add(new CommandData("emotes", "Gets a list of emotes the bot will react to."));
  }
}
