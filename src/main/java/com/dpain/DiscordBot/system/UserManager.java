package com.dpain.DiscordBot.system;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public class UserManager {
	private static UserManager ref;
	private static Guild guild;
	private Map<String, Entry> userMap = new HashMap<String, Entry>();
	
	private final String USERS_FILENAME = "users.yml";	
	private final Group DEFAULT_GROUP = Group.GUEST;
	
	private UserManager() {		
		try {
			readUsersFile();
		} catch (IOException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Did not have permission to create the " + USERS_FILENAME + " file!"));
		}
	}
	
	public static void setDefaultGuild(Guild guild) {
		UserManager.guild = guild;
	}
	
	public static UserManager loadGroupManager() {
		if(ref == null) {
			ref = new UserManager();
		}
		return ref;
	}
	
	public static UserManager load() {
		return loadGroupManager();
	}
	
	public Group getUserGroup(User user) {
		return userMap.get(user.getId()).group.get(0);
	}
	
	public void changeUserGroup(User user, Group group) {
		if(userMap.containsKey(user.getId())) {
			Entry info = userMap.get(user.getId());
			info.group.clear();
			info.group.add(group);
			saveConfig();
		} else {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "There is no such user with the user id: " + user.getId()));
		}
	}
	
	public void addUser(User user, Group group) {
		if(!userMap.containsKey(user.getId())) {
			Entry info = new Entry();
			info.username = user.getUsername();
			info.group.add(group);
			userMap.put(user.getId(), info);
			saveConfig();
		} else {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "There is already a user with the user id: " + user.getId()));
		}
	}
	
	public void addNewUser(User user) {
		if(!userMap.containsKey(user.getId())) {
			Entry info = new Entry();
			info.username = user.getUsername();
			info.group.add(DEFAULT_GROUP);
			userMap.put(user.getId(), info);
			saveConfig();
		} else {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "There is already a user with the user id: " + user.getId()));
		}
	}
	
	public void removeUser(User user) {
		if(userMap.containsKey(user.getId())) {
			userMap.remove(user.getId());
			saveConfig();
		} else {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "There was no user with the user id: " + user.getId()));
		}
	}
	
	private void readUsersFile() throws IOException {		
		CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(YamlPreferences.class.getClassLoader());
		constructor.addTypeDescription(new TypeDescription(YamlPreferences.class,"!userdata"));
		constructor.addTypeDescription(new TypeDescription(Group.class,"!Group"));
		Yaml yaml = new Yaml(constructor);
        
		try {
			YamlPreferences container = (YamlPreferences) yaml.load(new FileReader(USERS_FILENAME));
			userMap = container.users;
		} catch (FileNotFoundException | NullPointerException e) {
			saveConfig();
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Generated a new userdata file!"));
			rebuild(guild);
		}
		
	}
	
	public void saveConfig(){		
		Representer representer = new Representer();
		representer.addClassTag(YamlPreferences.class, new Tag("!userdata"));
		representer.addClassTag(Group.class, new Tag("!Group"));
		Yaml yaml = new Yaml(representer);
		
		try {
			YamlPreferences container = new YamlPreferences();
			container.users = userMap;
			
			yaml.dump(container, new FileWriter(USERS_FILENAME));
			
		} catch (IOException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Failed to read the config file!"));
		}
	}
	
	public void rebuild(Guild guild){
		// Clears the userMap
		userMap.clear();
		
		// Adds back all the users into the userMap
		for(User user : guild.getUsers()) {
			if(user.getId().equals(PropertiesManager.load().getValue(Property.OWNER_USER_ID))) {
				addUser(user, Group.OWNER);
			} else {
				addNewUser(user);
			}
		}
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Rebuit " + USERS_FILENAME + " file!"));
		saveConfig();
	}
	
	public void update(Guild guild){
		// Adds back all the users into the userMap
		for(User user : guild.getUsers()) {
			if(!userMap.containsKey(user.getId())) {
				addNewUser(user);
			} else if(user.getId().equals(PropertiesManager.load().getValue(Property.OWNER_USER_ID)) && userMap.get(user.getId()).group.get(0) != Group.OWNER) {
				changeUserGroup(user, Group.OWNER);
			}
			if(!user.getUsername().equals(userMap.get(user.getId()).username)) {
				userMap.get(user.getId()).username = user.getUsername();
			}
		}
		System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Updated " + USERS_FILENAME + " file!"));
		saveConfig();
	}
	
	public void reload() {
		try {
			readUsersFile();
		} catch (IOException e) {
			System.out.println(ConsolePrefixGenerator.getFormattedPrintln("UserManager", "Did not have permission to create the " + USERS_FILENAME + " file!"));
		}
	}
}
