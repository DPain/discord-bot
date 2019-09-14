package com.dpain.DiscordBot.plugin;

import java.time.format.DateTimeFormatter;
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
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class ModeratorPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(ModeratorPlugin.class);

  public ModeratorPlugin(EventWaiter waiter, DiscordBot bot) {
    super("ModeratorPlugin", Group.MODERATOR, waiter, bot);
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
            if (message.startsWith("-nick ")) {
              String param = message.substring(6);

              castedEvent.getGuild().getController()
                  .setNickname(castedEvent.getGuild().getSelfMember(), param).queue();
              castedEvent.getChannel().sendMessage("**Nickname changed to:** " + param).queue();

              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.equals("-channel")) {
              Map<String, String> channelInfo = new LinkedHashMap<String, String>();
              channelInfo.put("ID", castedEvent.getChannel().getId());
              channelInfo.put("Name", castedEvent.getChannel().getName());
              channelInfo.put("Topic", castedEvent.getChannel().getTopic());
              channelInfo.put("Channel Type", castedEvent.getChannel().getType().toString());
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
              String formattedDateTime =
                  castedEvent.getChannel().getCreationTime().format(formatter);
              channelInfo.put("Created Date", formattedDateTime + " " + Timezone.EST.getZoneId());
              channelInfo.put("NSFW", Boolean.toString(castedEvent.getChannel().isNSFW()));
              if (castedEvent.getChannel().getParent() != null) {
                channelInfo.put("Category", castedEvent.getChannel().getParent().getName());
                channelInfo.put("Category ID", castedEvent.getChannel().getParent().getId());
              }
              channelInfo.put("# of Pinned Messages",
                  Integer.toString(castedEvent.getChannel().getPinnedMessages().complete().size()));

              String formattedString = "";
              for (String category : channelInfo.keySet()) {
                formattedString += String.format("%s: %s\n", category, channelInfo.get(category));
              }
              formattedString = formattedString.trim();

              castedEvent.getChannel().sendMessage("**Channel Info:**\n" + formattedString).queue();
            } else if (message.equals("-randomnick")) {
              String[] names = {"Malfurion", "OpenAI", "Jaina", "Uther", "Anduin", "Valeera",
                  "Thrall", "Gul'dan", "Garrosh", "Medivh", "Dildo", "2B", "Toba", "Kizuna Ai"};

              Random ran = new Random();
              String tempName;
              while (true) {
                tempName = names[ran.nextInt(names.length)] + " Bot";
                if (castedEvent.getGuild().getSelfMember().getNickname() == null
                    || !castedEvent.getGuild().getSelfMember().getNickname().equals(tempName)) {
                  break;
                }
              }

              castedEvent.getGuild().getController()
                  .setNickname(castedEvent.getGuild().getSelfMember(), tempName).queue();
              castedEvent.getChannel().sendMessage("**Nickname changed to:** " + tempName).queue();

              logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
            } else if (message.startsWith("-clear ")) {
              String param = message.substring("-clear ".length());
              try {
                int i = Integer.parseInt(param);

                try {
                  Thread clearProcess = new Thread(new Cleaner(castedEvent.getChannel(), i));
                  clearProcess.start();
                } catch (RateLimitedException e) {
                  String temp = "The bot is being rate limited!";
                  castedEvent.getChannel().sendMessage(temp).queue();
                  logger.warn(LogHelper.elog(castedEvent, temp));
                }

                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
                // No point on sending a message that the cleaner is already running since it will
                // get instantly deleted.
              } catch (NumberFormatException e) {
                castedEvent.getChannel().sendMessage("**Please enter a correct number!**").queue();

                logger.warn(
                    LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.put("-nick *\\\"name\\\"*", "Changes the nickname of the bot.");
    super.commands.put("-channel", "Returns some info of the current Channel.");
    super.commands.put("-randomnick", "Randomly changes the nickname of the bot.");
    super.commands.put("-clear *\\\"x\\\"*", "Clears x amount of messages in the current text channel.");
  }

}
