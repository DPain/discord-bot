package com.dpain.DiscordBot.plugin;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Timezone;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.moderator.Cleaner;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class ModeratorPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(ModeratorPlugin.class);

  public ModeratorPlugin(EventWaiter waiter, DiscordBot bot) {
    super("ModeratorPlugin", Group.MODERATOR, waiter, bot);
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
        case "nick":
          String param = event.getOption("name").getAsString();

          event.getGuild().getSelfMember().modifyNickname(param).queue();;
          event.reply("**Nickname changed to:** " + param).queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));

          break;
        case "channel":
          Map<String, String> channelInfo = new LinkedHashMap<String, String>();
          channelInfo.put("ID", event.getTextChannel().getId());
          channelInfo.put("Name", event.getTextChannel().getName());
          channelInfo.put("Topic", event.getTextChannel().getTopic());
          channelInfo.put("Channel Type", event.getTextChannel().getType().toString());
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
          String formattedDateTime = event.getTextChannel().getTimeCreated().format(formatter);
          channelInfo.put("Created Date", formattedDateTime + " " + Timezone.EST.getZoneId());
          channelInfo.put("NSFW", Boolean.toString(event.getTextChannel().isNSFW()));
          if (event.getTextChannel().getParent() != null) {
            channelInfo.put("Category", event.getTextChannel().getParent().getName());
            channelInfo.put("Category ID", event.getTextChannel().getParent().getId());
          }
          channelInfo.put("# of Pinned Messages",
              Integer.toString(event.getTextChannel().retrievePinnedMessages().complete().size()));

          String formattedString = "";
          for (String category : channelInfo.keySet()) {
            formattedString += String.format("%s: %s\n", category, channelInfo.get(category));
          }
          formattedString = formattedString.trim();

          event.reply("**Channel Info:**\n" + formattedString).queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));

          break;
        case "randomnick":
          String[] names = {"Malfurion", "OpenAI", "Jaina", "Uther", "Anduin", "Valeera", "Thrall",
              "Gul'dan", "Garrosh", "Medivh", "Dildo", "2B", "Toba", "Kizuna Ai"};

          Random ran = new Random();
          String tempName;
          while (true) {
            tempName = names[ran.nextInt(names.length)] + " Bot";
            if (event.getGuild().getSelfMember().getNickname() == null
                || !event.getGuild().getSelfMember().getNickname().equals(tempName)) {
              break;
            }
          }

          event.getGuild().getSelfMember().modifyNickname(tempName).queue();
          event.reply("**Nickname changed to:** " + tempName).queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));

          break;
        case "clear":
          event.deferReply(true).queue();
          Long number = event.getOption("number").getAsLong();
          try {
            int i = Math.toIntExact(number);

            try {
              Thread clearProcess = new Thread(new Cleaner(event, i));
              clearProcess.start();
            } catch (RateLimitedException e) {
              String temp = "The bot is being rate limited!";
              event.getTextChannel().sendMessage(temp).queue();
              logger.warn(LogHelper.elog(event, temp));
            }
            
            event.getHook().sendMessage("Executed Async Command!").queue();

            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } catch (NumberFormatException e) {
            event.getHook().sendMessage("**Please enter a correct number!**").queue();

            logger.warn(LogHelper.elog(event, String.format("Incorrect command: %s", message)));
          }
          
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("nick", "Changes the nickname of the bot.")
        .addOption(OptionType.STRING, "name", "Name to set for the bot", true));
    super.commands.add(new CommandData("channel", "Returns some info of the current Channel."));
    super.commands.add(new CommandData("randomnick", "Randomly changes the nickname of the bot."));
    super.commands.add(new CommandData("clear", "Removes messages in the text channel.")
        .addOption(OptionType.INTEGER, "number", "Number of messages to process.", true));
  }

}
