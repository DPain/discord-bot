package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CustomCommandPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(CustomCommandPlugin.class);

  public CustomCommandPlugin(EventWaiter waiter, DiscordBot bot) {
    super("CustomCommandPlugin", Group.TRUSTED_USER, waiter, bot);
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    try {
      String message = event.getMessage().getContentRaw();

      if (canAccessPlugin(event.getMember()) && !event.getAuthor().getId()
          .equals(event.getJDA().getSelfUser().getId())) {

        if (message.startsWith("!")) {
          /*
           * #TODO Implement
           */
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
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
        case "custom":
          event.deferReply().queue();
          
          /*
           * #TODO Implement
           */
          event.getHook().sendMessage("WIP").queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        case "custom delete":
          event.deferReply().queue();
          
          /*
           * #TODO Implement
           */
          event.getHook().sendMessage("WIP").queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        case "custom set":
          event.deferReply().queue();
          
          /*
           * #TODO Implement
           */
          event.getHook().sendMessage("WIP").queue();
          
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("custom",
        "Displays all the custom commands created by the users from the server."));
    super.commands.add(new CommandData("custom delete", "Deletes a custom command.")
        .addOption(OptionType.STRING, "name", "Name of the custom command", true));
    super.commands.add(new CommandData("custom set", "Sets a custom command.")
        .addOption(OptionType.STRING, "name", "Name of the custom command", true).addOption(
            OptionType.SUB_COMMAND, "text", "The text to print when custom command is executed."));
  }
}
