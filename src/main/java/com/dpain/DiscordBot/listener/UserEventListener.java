package com.dpain.DiscordBot.listener;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.twitch.TwitchAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import com.dpain.DiscordBot.system.PropertiesManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class UserEventListener implements EventListener {
  private final static Logger logger = LoggerFactory.getLogger(UserEventListener.class);

  private static TwitchAlerter alerter;
  private static Guild guild;
  
  private DiscordBot bot;

  public UserEventListener(DiscordBot bot) {
    logger.info("Listening User Events!");
    this.bot = bot;
  }

  private boolean isTrustedTwitchStreamer(Member member) {
    return MemberManager.load().getMemberGroup(member).getHierarchy() <= Group.TRUSTED_USER
        .getHierarchy();
  }

  public static void setDefaultGuild(Guild guild) {
    UserEventListener.guild = guild;
    instantiateTwitchAlerter();
  }

  public static void instantiateTwitchAlerter() {
    alerter = new TwitchAlerter(guild);
  }

  @Override
  public void onEvent(GenericEvent event) {
    if (event instanceof GuildMemberJoinEvent) {
      GuildMemberJoinEvent castedEvent = (GuildMemberJoinEvent) event;

      MemberManager.load().addMember(castedEvent.getMember());

      logger.info(LogHelper.elog(castedEvent, "User joined!"));
    } else if (event instanceof GuildBanEvent) {
      GuildBanEvent castedEvent = (GuildBanEvent) event;

      MemberManager.load().changeMemberGroup(guild.getMember(castedEvent.getUser()),
          Group.PRISONER);

      logger.info(LogHelper.elog(castedEvent, "User is banned!"));
    } else if (event instanceof GuildMemberRemoveEvent) {
      GuildMemberRemoveEvent castedEvent = (GuildMemberRemoveEvent) event;

      logger.info(LogHelper.elog(castedEvent, "User left!"));
    } else if (event instanceof GuildUnbanEvent) {
      GuildUnbanEvent castedEvent = (GuildUnbanEvent) event;

      logger.info(LogHelper.elog(castedEvent, "User is unbanned!"));
    } else if (event instanceof UserUpdateActivityOrderEvent) {
      UserUpdateActivityOrderEvent castedEvent = (UserUpdateActivityOrderEvent) event;
      if (!castedEvent.getGuild().getMember(castedEvent.getUser()).getActivities().isEmpty()) {
        List<Activity> activities =
            castedEvent.getGuild().getMember(castedEvent.getUser()).getActivities();
        for (Activity item : activities) {
          String url = item.getUrl();
          if (url != null && Activity.isValidStreamingUrl(url)) {
            if (isTrustedTwitchStreamer(guild.getMember(castedEvent.getUser()))
                && !castedEvent.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
              if (PropertiesManager.load().getValue(Property.USE_TWITCH_ALERTER).equals("true")) {
                alerter.notifyTwitchStream(guild.getMember(castedEvent.getUser()));
              }
              logger.info(LogHelper.elog(castedEvent, "User is streaming in Twitch.tv."));
            }
          }
        }
      }
    } else if (event instanceof GuildJoinEvent) {
      GuildJoinEvent castedEvent = (GuildJoinEvent) event;
      
      bot.pluginListener.registerCommands(castedEvent.getGuild());
      logger.info(LogHelper.elog(castedEvent, "Bot joined a server!"));
    }
  }
}
