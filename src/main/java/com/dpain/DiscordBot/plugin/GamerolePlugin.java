package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.NoPermissionException;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.RoleManager;

public class GamerolePlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(GamerolePlugin.class);

  public GamerolePlugin(EventWaiter waiter, DiscordBot bot) {
    super("GamerolePlugin", Group.GUEST, waiter, bot);
  }

  @Override
  public void handleEvent(GenericEvent event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if (canAccessPlugin(castedEvent.getMember()) && !castedEvent.getAuthor().getId()
            .equals(castedEvent.getJDA().getSelfUser().getId())) {
          if (message.startsWith("-")) {
            if (message.equals("-gamerole")) {
              List<Role> roles = castedEvent.getGuild().getRoles();
              LinkedList<String> output = new LinkedList<String>();
              for (Role role : roles) {
                /**
                 * The conditions to be considered a gamerole. All permissions from the gamerole
                 * must be identical to the PublicRole.
                 */
                if (role.getPermissions()
                    .equals(castedEvent.getGuild().getPublicRole().getPermissions())
                    && role.getName().equals(role.getName().toUpperCase())) {
                  output.add(role.getName());
                }
              }

              if (output.size() > 0) {
                MessageHelper.sendPage("**Game Roles: **",
                    output.toArray(new String[output.size()]), 3, 50, waiter,
                    castedEvent.getChannel(), 1, TimeUnit.HOURS);
                logger.info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
              } else {
                castedEvent.getChannel().sendMessage("There are no Game Roles in this Server!")
                    .queue();
              }
            } else if (message.startsWith("-gamerole ")) {
              String param = message.substring(10);
              if (param.startsWith("add ")) {
                String gameName = param.substring(4).toUpperCase();
                if (gameName != "") {
                  RoleManager gameroleManager = null;
                  // Checks if Role does not already exist.
                  if (!castedEvent.getGuild().getRolesByName(gameName, true).isEmpty()) {
                    // gameroles must not have any permission to prevent exploitations.
                    if (castedEvent.getGuild().getRolesByName(gameName, true).get(0)
                        .getPermissions()
                        .equals(castedEvent.getGuild().getPublicRole().getPermissions())) {
                      gameroleManager =
                          castedEvent.getGuild().getRolesByName(gameName, true).get(0).getManager();
                      castedEvent.getGuild()
                          .addRoleToMember(castedEvent.getMember(), gameroleManager.getRole())
                          .queue();
                      castedEvent.getChannel().sendMessage("Added the Gamerole!").queue();
                      logger
                          .info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
                    } else {
                      castedEvent.getChannel()
                          .sendMessage("You're not allowed to choose a role that is not a game!")
                          .queue();
                      logger.error(LogHelper.elog(castedEvent, String
                          .format("Attempted to add himself to non-gamerole role: %s", message)));
                      throw new NoPermissionException();
                    }
                  }
                } else {
                  logger.warn(
                      LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
                }
              } else if (param.startsWith("remove ")) {
                String gameName = param.substring(7).toUpperCase();
                if (gameName != "") {
                  RoleManager gameroleManager = null;

                  // Makes sure gamerole exists.
                  if (!castedEvent.getGuild().getRolesByName(gameName, true).isEmpty()) {
                    // gameroles must not have any permission to prevent exploitations.
                    if (castedEvent.getGuild().getRolesByName(gameName, true).get(0)
                        .getPermissions()
                        .equals(castedEvent.getGuild().getPublicRole().getPermissions())) {
                      gameroleManager =
                          castedEvent.getGuild().getRolesByName(gameName, true).get(0).getManager();
                      castedEvent.getGuild()
                          .removeRoleFromMember(castedEvent.getMember(), gameroleManager.getRole())
                          .queue();
                      castedEvent.getChannel().sendMessage("Removed the Gamerole!").queue();
                      logger
                          .info(LogHelper.elog(castedEvent, String.format("Command: %s", message)));
                    } else {
                      logger.error(LogHelper.elog(castedEvent, String.format(
                          "Attempted to remove himself to non-gamerole role: %s", message)));
                      throw new NoPermissionException();
                    }
                  }
                } else {
                  logger.warn(
                      LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
                }
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
    super.commands.put("-gamerole", "Returns a list of available Game Roles in the server.");
    super.commands.put("-gamerole add *\\\"name\\\"*", "Add yourself to the gamerole.");
    super.commands.put("-gamerole remove *\\\"name\\\"*", "Remove yourself from the gamerole.");
  }
}
