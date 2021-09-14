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
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class ProfanityGuardPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(ProfanityGuardPlugin.class);

  private boolean activated = false;

  /**
   * #TODO In future, implement strike out system and save into a file
   */

  public ProfanityGuardPlugin(EventWaiter waiter, DiscordBot bot) {
    super("ProfanityGuardPlugin", Group.MODERATOR, waiter, bot);
  }
  
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    String message = event.getMessage().getContentRaw();

    if ((event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
        || canAccessPlugin(event.getMember())) {
      /*
       * # TODO Implement if() {
       * 
       * }
       */
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
        case "profguard enable": {
          event.deferReply(true).queue();
          
          activated = true;
          event.getHook().sendMessage("*Profanity Guard is ENABLED!*").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "profguard disable": {
          event.deferReply(true).queue();
          
          activated = false;
          event.getHook().sendMessage("*Profanity Guard is DISABLED!*").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "profguard offenders": {
          event.deferReply(true).queue();
          
          event.getHook().sendMessage("Acknowledged Command! Sending a private message.").queue();
          
          event.getUser().openPrivateChannel().complete().sendMessage("WIP").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("profguard enable", "Enables Profanity Guard."));
    super.commands.add(new CommandData("profguard disable", "Disables Profanity Guard."));
    super.commands.add(new CommandData("profguard offenders",
        "PMs the list of offenders to the one who issued the command. WIP."));
  }

}
