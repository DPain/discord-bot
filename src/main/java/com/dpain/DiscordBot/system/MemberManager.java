package com.dpain.DiscordBot.system;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MemberManager {
  private final static Logger logger = LoggerFactory.getLogger(MemberManager.class);

  private static MemberManager ref;
  private static Guild guild;
  private Map<String, Entry> memberMap = new HashMap<String, Entry>();

  private final String USERS_FILENAME = "users.yml";
  private final Group DEFAULT_GROUP = Group.GUEST;

  private MemberManager() {
    try {
      readMembersFile();
    } catch (IOException e) {
      logger.error("Did not have permission to create the " + USERS_FILENAME + " file!");
    }
  }

  public static void setDefaultGuild(Guild guild) {
    MemberManager.guild = guild;
  }

  public static MemberManager loadGroupManager() {
    if (ref == null) {
      ref = new MemberManager();
    }
    return ref;
  }

  public static MemberManager load() {
    return loadGroupManager();
  }

  public Group getMemberGroup(Member member) {
    Entry entry = memberMap.get(member.getUser().getId());
    if(entry == null) {
      // Somehow member wasn't added to the memberMap.
      addMember(member, DEFAULT_GROUP);
    }
    return memberMap.get(member.getUser().getId()).group.get(0);
  }

  public void changeMemberGroup(Member member, Group group) {
    if (memberMap.containsKey(member.getUser().getId())) {
      Entry info = memberMap.get(member.getUser().getId());
      info.group.clear();
      info.group.add(group);
    } else {
      logger.error("There is no such member with the member id: " + member.getUser().getId());
    }
  }

  public void addMember(Member member, Group group) {
    if (!memberMap.containsKey(member.getUser().getId())) {
      Entry info = new Entry();
      info.username = member.getUser().getName();
      info.nickname = member.getNickname();
      info.group.add(group);
      memberMap.put(member.getUser().getId(), info);
    } else {
      logger.error("There is already a member with the member id: " + member.getUser().getId());
    }
  }

  public void addMember(Member member) {
    if (!memberMap.containsKey(member.getUser().getId())) {
      Entry info = new Entry();
      info.username = member.getUser().getName();
      info.nickname = member.getNickname();
      info.group.add(DEFAULT_GROUP);
      memberMap.put(member.getUser().getId(), info);
    } else {
      logger.error("There is already a member with the member id: " + member.getUser().getId());
    }
  }

  public void removeMember(Member member) {
    if (memberMap.containsKey(member.getUser().getId())) {
      memberMap.remove(member.getUser().getId());
      saveConfig();
    } else {
      logger.error("There is no member with the member id: " + member.getUser().getId());
    }
  }

  private void readMembersFile() throws IOException {
    CustomClassLoaderConstructor constructor =
        new CustomClassLoaderConstructor(YamlPreferences.class.getClassLoader());
    constructor.addTypeDescription(new TypeDescription(YamlPreferences.class, "!userdata"));
    constructor.addTypeDescription(new TypeDescription(Group.class, "!group"));
    Yaml yaml = new Yaml(constructor);

    try {
      YamlPreferences container = (YamlPreferences) yaml.load(new FileReader(USERS_FILENAME));
      memberMap = container.members;
    } catch (FileNotFoundException | NullPointerException e) {
      saveConfig();
      logger.info("Generated a new userdata file!");
      rebuild(guild);
    } catch (YAMLException e) {
      logger.error("Corrupted userdata file!");
      logger.error(e.getMessage());
      System.exit(1);
    }
  }

  public void saveConfig() {
    Representer representer = new Representer();
    representer.addClassTag(YamlPreferences.class, new Tag("!userdata"));
    representer.addClassTag(Group.class, new Tag("!group"));
    Yaml yaml = new Yaml(representer);

    try {
      YamlPreferences container = new YamlPreferences();
      container.members = memberMap;

      yaml.dump(container, new FileWriter(USERS_FILENAME));
    } catch (IOException e) {
      logger.error("Failed to read the config file!");
    }
  }

  public void rebuild(Guild guild) {
    // Clears the memberMap
    memberMap.clear();

    // Adds back all the members into the memberMap
    for (Member member : guild.getMembers()) {
      if (member.getUser().getId()
          .equals(PropertiesManager.load().getValue(Property.OWNER_USER_ID))) {
        addMember(member, Group.OWNER);
      } else if (member.getUser().getId().equals(guild.getJDA().getSelfUser().getId())) {
        addMember(member, Group.BOT);
      } else {
        addMember(member);
      }
    }
    logger.info("Rebuit " + USERS_FILENAME + " file!");
    saveConfig();
  }

  public void update(Guild guild) {
    // Compares all the members from the server and updates the memberMap
    for (Member member : guild.getMembers()) {
      if (member.getUser().getId()
          .equals(PropertiesManager.load().getValue(Property.OWNER_USER_ID))) {
        // Member is the owner
        logger.info("This is the owner!");
        if (!memberMap.containsKey(member.getUser().getId())) {
          addMember(member, Group.OWNER);
        }
        memberMap.get(member.getUser().getId()).nickname = member.getNickname();
        changeMemberGroup(member, Group.OWNER);
      } else if (!memberMap.containsKey(member.getUser().getId())) {
        // Member is not in the userdata file
        logger.info("User does not exist and will be added into the userdata file!");
        addMember(member);
      } else {
        // Displays existing member data change from previous memberMap check.
        if (!member.getUser().getName().equals(memberMap.get(member.getUser().getId()).username)) {
          logger.info(String.format("Member: %s (username: %s - %s) changed his username to: %s",
              member.getNickname(), memberMap.get(member.getUser().getId()).username,
              member.getUser().getId(), member.getUser().getName()));
          memberMap.get(member.getUser().getId()).username = member.getUser().getName();
        }
        if (member.getNickname() != null
            && !member.getNickname().equals(memberMap.get(member.getUser().getId()).nickname)) {
          logger.info(String.format("Member: %s (username: %s - %s) changed his nickname to: %s",
              memberMap.get(member.getUser().getId()).nickname, member.getUser().getName(),
              member.getUser().getId(), member.getNickname()));
          memberMap.get(member.getUser().getId()).nickname = member.getNickname();
        }
      }
    }
    logger.info("Updated " + USERS_FILENAME + " file!");

    // Saves the updated memberMap data to the userdata file
    saveConfig();
  }

  public void reload() {
    try {
      readMembersFile();
    } catch (IOException e) {
      logger.error("Did not have permission to create the " + USERS_FILENAME + " file!");
    }
  }
}
