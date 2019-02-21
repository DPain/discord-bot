package com.dpain.DiscordBot.listener.twitch;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class TwitchAlerter {
  private Guild guild;

  public TwitchAlerter(Guild guild) {
    this.guild = guild;
  }

  public void notifyTwitchStream(Member member) {
    guild.getDefaultChannel()
        .sendMessage(member.getEffectiveName() + " started streaming in Twitch!");
  }
}
