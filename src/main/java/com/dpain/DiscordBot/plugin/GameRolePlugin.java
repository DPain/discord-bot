package com.dpain.DiscordBot.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.exception.NoPermissionException;
import com.dpain.DiscordBot.system.ConsolePrefixGenerator;

import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.managers.RoleManager;

public class GameRolePlugin extends Plugin {

	public GameRolePlugin() {
		super("GameRolePlugin", Group.TRUSTED_USER);
		super.helpString = "**GameRole Plugin Usage:** \n"
		+ "-gameRole add *\"name\"* : Add yourself to the GameRole.\n"
		+ "-gameRole remove *\"name\"* : Remove yourself from the GameRole.\n";
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();

				if (canAccessPlugin(castedEvent.getAuthor())
						&& !castedEvent.getAuthor().getId().equals(castedEvent.getJDA().getSelfInfo().getId())) {
					if (message.startsWith("-")) {
						if (message.equals("-gameRole")) {
							List<Role> roles = castedEvent.getGuild().getRoles();
							LinkedList<String> output = new LinkedList<String>();
							for (Role role : roles) {
								// The conditions to be considered a GameRole.
								if(role.getPermissions().isEmpty() && role.getName().equals(role.getName().toUpperCase())) {
									output.add(role.getName());
								}
							}
							castedEvent.getChannel().sendMessage("Game Roles: " + Arrays.toString(output.toArray()));
						} else if (message.startsWith("-gameRole ")) {
							String param = message.substring(10);
							if (param.startsWith("add ")) {
								String gameName = param.substring(4).toUpperCase();
								if (gameName != "") {
									RoleManager gameRoleManager = null;
									// Checks if Role does not already exist.
									if (!castedEvent.getGuild().getRolesByName(gameName).isEmpty()) {
										// GameRoles must not have any permission to prevent exploitations.
										if (castedEvent.getGuild().getRolesByName(gameName).get(0).getPermissions().isEmpty()) {
											gameRoleManager = castedEvent.getGuild().getRolesByName(gameName).get(0).getManager();
											castedEvent.getGuild().getManager().addRoleToUser(castedEvent.getAuthor(), gameRoleManager.getRole());

											castedEvent.getGuild().getManager().update();
										} else {
											System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "You're not allowed to choose a role that is not a game!"));
											throw new NoPermissionException();
										}
									}
								} else {
									System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "You're not allowed to have an empty game name!"));
								}
							} else if (param.startsWith("remove ")) {
								String gameName = param.substring(7).toUpperCase();
								if (gameName != "") {
									RoleManager gameRoleManager = null;

									// Checks if Role does not already exist.
									if (!castedEvent.getGuild().getRolesByName(gameName).isEmpty()) {
										// GameRoles must not have any permission to prevent exploitations.
										if (castedEvent.getGuild().getRolesByName(gameName).get(0).getPermissions().isEmpty()) {
											gameRoleManager = castedEvent.getGuild().getRolesByName(gameName).get(0).getManager();
											castedEvent.getGuild().getManager().removeRoleFromUser(castedEvent.getAuthor(), gameRoleManager.getRole());
											
											castedEvent.getGuild().getManager().update();
										} else {
											System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "You're not allowed to choose a role that is not a game!"));
											throw new NoPermissionException();
										}
									}
								} else {
									System.out.println(ConsolePrefixGenerator.getFormattedPrintln(this.getName(), "You're not allowed to have an empty game name!"));
								}
							} else {
								castedEvent.getChannel().sendMessage(helpString);
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
