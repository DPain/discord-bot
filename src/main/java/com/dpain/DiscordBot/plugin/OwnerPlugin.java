package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class OwnerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(OwnerPlugin.class);

  public OwnerPlugin(EventWaiter waiter, DiscordBot bot) {
    super("OwnerPlugin", Group.OWNER, waiter, bot);
  }

  @Override
  public void onSlashCommand(SlashCommandEvent event) {
    if (canAccessPlugin(event.getMember())
        && !event.getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
      String message = String.format("CMD: %s - %s", event.getName(),
          Arrays.toString(event.getOptions().toArray()));

      // Only accept commands from guilds.
      if (event.getGuild() == null) {
        return;
      }
      switch (event.getName()) {
        case "username": {
          String name = event.getOption("name").getAsString();
          event.getJDA().getSelfUser().getManager().setName(name);
          event.reply("**Username changed to:** " + name).queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "group": {
          event.deferReply(true).queue();

          String userId = event.getOption("user-id").getAsString();
          String groupName = event.getOption("group-name").getAsString();

          /**
           * #TODO Implement user change group feature. Also fix userdata.yml constant read issue.
           */

          event.getHook().sendMessage("WIP").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "info": {
          // Seems like owner is the original owner (not current owner).
          Member owner = event.getGuild().getOwner();
          String nickName = owner == null ? "Owner no longer in server." : (owner.getNickname() == null ? "NONE" : owner.getNickname());
          String result = String.format(
              "**Server Info:**\nName: %s\nID: %s\nOwner Nickname: %s\nOwner ID: %s",
              event.getGuild().getName(), event.getGuild().getId(),
              nickName, event.getGuild().getOwner().getUser().getId());
          event.reply(result).queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "rebuild": {
          event.deferReply(true).queue();
          MemberManager.load().rebuild(event.getGuild());
          event.getHook().sendMessage("Rebuilt userdata.yml file for this guild.").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "update": {
          event.deferReply(true).queue();
          MemberManager.load().update(event.getGuild());
          event.getHook().sendMessage("Rebuilt userdata.yml file for this guild.").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "reload": {
          event.deferReply(true).queue();
          MemberManager.load().reload();
          event.getHook().sendMessage("Rebuilt userdata.yml file for this guild.").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "exit": {
          event.reply("Received Command! Will shutdown.").queue();
          MemberManager.load().saveConfig();
          G2gAlerter.load().saveConfig();
          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          event.getJDA().shutdown();
          System.exit(0);
          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("username", "Changes the username of the bot.")
        .addOption(OptionType.STRING, "name", "Username to set for the bot", true));
    super.commands.add(new CommandData("group", "Changes the user's group. WIP.")
        .addOption(OptionType.STRING, "user-id", "User's user ID.", true)
        .addOption(OptionType.STRING, "group-name", "Name of the group to assign the user.", true));
    super.commands.add(new CommandData("info", "Displays the server info."));
    super.commands.add(new CommandData("rebuild", "Resets the userdata file to default."));
    super.commands.add(new CommandData("update", "Updates the userdata file."));
    super.commands.add(new CommandData("reload", "Reads the userdata file again."));
    super.commands.add(new CommandData("exit", "Shutsdown the bot."));
  }

}
