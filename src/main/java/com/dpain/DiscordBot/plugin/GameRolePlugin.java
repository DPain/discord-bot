package com.dpain.DiscordBot.plugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.NoPermissionException;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.RoleManager;

public class GameRolePlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(GameRolePlugin.class.getName());

	public GameRolePlugin() {
		super("GameRolePlugin", Group.GUEST);
		super.helpString = "**GameRole Plugin Usage:** \n"
		+ "-gameRole add *\"name\"* : Add yourself to the GameRole.\n"
		+ "-gameRole remove *\"name\"* : Remove yourself from the GameRole.\n";
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContentRaw();

				if (canAccessPlugin(castedEvent.getMember()) && !castedEvent.getAuthor().getId().equals(castedEvent.getJDA().getSelfUser().getId())) {
					if (message.startsWith("-")) {
						if (message.equals("-gameRole")) {
							List<Role> roles = castedEvent.getGuild().getRoles();
							LinkedList<String> output = new LinkedList<String>();
							for (Role role : roles) {
								/**
								 * The conditions to be considered a GameRole.
								 * All permissions from the GameRole must be identical to the PublicRole.
								 */
								if(role.getPermissions().equals(castedEvent.getGuild().getPublicRole().getPermissions()) && role.getName().equals(role.getName().toUpperCase())) {
									output.add(role.getName());
								}
							}
							castedEvent.getChannel().sendMessage("Game Roles: " + Arrays.toString(output.toArray())).queue();
							logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
						} else if (message.startsWith("-gameRole ")) {
							String param = message.substring(10);
							if (param.startsWith("add ")) {
								String gameName = param.substring(4).toUpperCase();
								if (gameName != "") {
									RoleManager gameRoleManager = null;
									// Checks if Role does not already exist.
									if (!castedEvent.getGuild().getRolesByName(gameName, true).isEmpty()) {
										// GameRoles must not have any permission to prevent exploitations.
										if (castedEvent.getGuild().getRolesByName(gameName, true).get(0).getPermissions().equals(castedEvent.getGuild().getPublicRole().getPermissions())) {
											gameRoleManager = castedEvent.getGuild().getRolesByName(gameName, true).get(0).getManager();
											castedEvent.getGuild().getController().addRolesToMember(castedEvent.getMember(), gameRoleManager.getRole()).queue();
											logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
										} else {
											castedEvent.getChannel().sendMessage("You're not allowed to choose a role that is not a game!").queue();
											logger.log(Level.SEVERE, LogHelper.elog(castedEvent, String.format("Attempted to add himself to non-GameRole role: %s", message)));
											throw new NoPermissionException();
										}
									}
								} else {
									logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
								}
							} else if (param.startsWith("remove ")) {
								String gameName = param.substring(7).toUpperCase();
								if (gameName != "") {
									RoleManager gameRoleManager = null;

									// Makes sure GameRole exists.
									if (!castedEvent.getGuild().getRolesByName(gameName, true).isEmpty()) {
										// GameRoles must not have any permission to prevent exploitations.
										if (castedEvent.getGuild().getRolesByName(gameName, true).get(0).getPermissions().equals(castedEvent.getGuild().getPublicRole().getPermissions())) {
											gameRoleManager = castedEvent.getGuild().getRolesByName(gameName, true).get(0).getManager();
											castedEvent.getGuild().getController().removeRolesFromMember(castedEvent.getMember(), gameRoleManager.getRole()).queue();
											logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
										} else {
											logger.log(Level.SEVERE, LogHelper.elog(castedEvent, String.format("Attempted to remove himself to non-GameRole role: %s", message)));
											throw new NoPermissionException();
										}
									}
								} else {
									logger.log(Level.WARNING, LogHelper.elog(castedEvent, String.format("Incorrect command: %s", message)));
								}
							} else {
								castedEvent.getChannel().sendMessage(helpString).queue();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
