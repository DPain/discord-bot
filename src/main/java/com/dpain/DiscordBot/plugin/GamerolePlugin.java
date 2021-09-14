package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.RoleManager;

public class GamerolePlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(GamerolePlugin.class);

  public GamerolePlugin(EventWaiter waiter, DiscordBot bot) {
    super("GamerolePlugin", Group.GUEST, waiter, bot);
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
        case "gamerole":
          event.deferReply(true).queue();
          
          List<Role> roles = event.getGuild().getRoles();
          LinkedList<String> output = new LinkedList<String>();
          for (Role role : roles) {
            /**
             * The conditions to be considered a gamerole. All permissions from the gamerole
             * must be identical to the PublicRole.
             */
            if (role.getPermissions()
                .equals(event.getGuild().getPublicRole().getPermissions())
                && role.getName().equals(role.getName().toUpperCase())) {
              output.add(role.getName());
            }
          }

          if (output.size() > 0) {
            MessageHelper.sendPage("**Game Roles: **",
                output.toArray(new String[output.size()]), 3, 50, waiter,
                event.getTextChannel(), 1, TimeUnit.HOURS);
            
            event.getHook().sendMessage("Processed Command!").queue();
            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } else {
            event.getHook().sendMessage("There are no Game Roles in this Server!").queue();
          }
          
          break;
        case "gamerole-add":
          event.deferReply().queue();
          
          Role role = event.getOption("role").getAsRole();
          RoleManager gameroleManager;
          
          // Gameroles must not have any permission to prevent exploitations.
          if (role.getPermissions().equals(event.getGuild().getPublicRole().getPermissions())) {
            gameroleManager = role.getManager();
            event.getGuild().addRoleToMember(event.getMember(), gameroleManager.getRole()).queue();
            event.getHook().sendMessage("Added the Gamerole!").queue();

            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } else {
            event.getHook().sendMessage("You're not allowed to choose a role that is not a game!").queue();
            
            logger.warn(LogHelper.elog(event, String.format("Attempted to add himself to non-gamerole role: %s", message)));
          }
          
          break;
        case "gamerole-remove":
          event.deferReply().queue();
          
          role = event.getOption("role").getAsRole();
          
          // Gameroles must not have any permission to prevent exploitations.
          if (role.getPermissions().equals(event.getGuild().getPublicRole().getPermissions())) {
            gameroleManager = role.getManager();
            event.getGuild().removeRoleFromMember(event.getMember(), gameroleManager.getRole()).queue();
            event.getHook().sendMessage("Removed the Gamerole!").queue();

            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          } else {
            event.getHook().sendMessage("You're not allowed to choose a role that is not a game!").queue();
            
            logger.warn(LogHelper.elog(event, String.format("Attempted to remove himself from non-gamerole role: %s", message)));
          }
          
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands
        .add(new CommandData("gamerole", "Returns a list of available Gameroles in the server."));
    super.commands
        .add(new CommandData("gamerole-add", "Add yourself to the gamerole.").addOption(
            OptionType.ROLE, "role",
            "The Gamerole to be assigned.", true));
    super.commands
      .add(new CommandData("gamerole-remove", "Remove yourself from the gamerole.").addOption(
          OptionType.ROLE, "role",
          "The Gamerole to be removed.", true));
  }
}
