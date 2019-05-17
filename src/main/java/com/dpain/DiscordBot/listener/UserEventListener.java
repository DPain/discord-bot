package com.dpain.DiscordBot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.twitch.TwitchAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import com.dpain.DiscordBot.system.PropertiesManager;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class UserEventListener implements EventListener {
  private final static Logger logger = LoggerFactory.getLogger(UserEventListener.class);

  private static TwitchAlerter alerter;
  private static Guild guild;

  public UserEventListener() {
    logger.info("Listening User Events!");
  }

  @Override
  public void onEvent(Event event) {
    if (event instanceof GuildMemberJoinEvent) {
      GuildMemberJoinEvent castedEvent = (GuildMemberJoinEvent) event;

      MemberManager.load().addMember(castedEvent.getMember());

      logger.info(LogHelper.elog(castedEvent, "User joined!"));
    } else if (event instanceof GuildBanEvent) {
      GuildBanEvent castedEvent = (GuildBanEvent) event;

      MemberManager.load().changeMemberGroup(guild.getMember(castedEvent.getUser()),
          Group.PRISONER);

      logger.info(LogHelper.elog(castedEvent, "User is banned!"));
    } else if (event instanceof GuildMemberLeaveEvent) {
      GuildMemberLeaveEvent castedEvent = (GuildMemberLeaveEvent) event;

      logger.info(LogHelper.elog(castedEvent, "User left!"));
    } else if (event instanceof GuildUnbanEvent) {
      GuildUnbanEvent castedEvent = (GuildUnbanEvent) event;

      logger.info(LogHelper.elog(castedEvent, "User is unbanned!"));
    } else if (event instanceof UserUpdateGameEvent) {
      UserUpdateGameEvent castedEvent = (UserUpdateGameEvent) event;
      if (castedEvent.getGuild().getMember(castedEvent.getUser()).getGame() != null) {
        String temp = castedEvent.getGuild().getMember(castedEvent.getUser()).getGame().getUrl();
        if (temp != null && Game.isValidStreamingUrl(temp)) {
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
}
