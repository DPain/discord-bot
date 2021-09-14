package com.dpain.DiscordBot.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.audioplayer.GuildMusicManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;

public class AudioPlayerPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(AudioPlayerPlugin.class);

  private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  private final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

  public AudioPlayerPlugin(EventWaiter waiter, DiscordBot bot) {
    super("AudioPlayerPlugin", Group.USER, waiter, bot);

    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler((AudioSendHandler) musicManager.getSendHandler());

    return musicManager;
  }

  private void setVolume(final TextChannel channel, int volume) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    musicManager.getPlayer().setVolume(volume);
  }

  private int getVolume(final TextChannel channel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    return musicManager.getPlayer().getVolume();
  }

  private void loadAndPlay(final SlashCommandEvent event, final String trackUrl) {
    GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        play(musicManager, track);

        event.reply(String.format("Added \"%s\" to the queue!", track.getInfo().title)).queue();
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track : playlist.getTracks()) {
          play(musicManager, track);
        }

        event.reply(String.format("Added playlist \"%s\" with %d songs to the queue!",
            playlist.getName(), playlist.getTracks().size())).queue();
      }

      @Override
      public void noMatches() {
        event.reply("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        event.reply("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  private String[] getTrackInfos(BlockingQueue<AudioTrack> list) {
    ArrayList<String> output = new ArrayList<String>();

    Iterator<AudioTrack> iter = list.iterator();
    while (iter.hasNext()) {
      AudioTrack track = iter.next();

      output.add(String.format("%s - by %s", track.getInfo().title, track.getInfo().author));
    }

    return output.toArray(new String[output.size()]);
  }

  private void play(GuildMusicManager musicManager, AudioTrack track) {
    musicManager.queue(track);
  }

  private void skipTrack(SlashCommandEvent event, int num) {
    if (num > 0) {
      GuildMusicManager musicMgr = getGuildAudioPlayer(event.getGuild());
      musicMgr.nextTrack(num);

      event.reply("Skipped tracks.").queue();
    } else {
      // Skipping 0 or negative numbers of tracks.
      event.reply("You can't skip tracks like that!").queue();
    }
  }

  private void skipAllTrack(SlashCommandEvent event) {
    GuildMusicManager musicMgr = getGuildAudioPlayer(event.getGuild());
    musicMgr.clear();
    musicMgr.nextTrack(1);

    event.reply("Cleared the Playlist.").queue();
  }

  private void shufflePlaylist(SlashCommandEvent event) {
    GuildMusicManager musicMgr = getGuildAudioPlayer(event.getGuild());
    musicMgr.shuffle();

    event.reply("Shuffled the Playlist.").queue();
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
        case "join": {
          // Joins the voice channel the user was in.

          VoiceChannel channelToJoin = null;
          SortedSnowflakeCacheView<VoiceChannel> vChannels =
              event.getGuild().getVoiceChannelCache();
          Member requestedMember = event.getMember();
          for (VoiceChannel channel : vChannels) {
            if (channel.getMembers().contains(requestedMember)) {
              channelToJoin = channel;
              break;
            }
          }

          if (channelToJoin == null) {
            // User isn't in any voice channel.
            event.reply("You are not in any Voice Channel!").queue();
          } else {
            // Found the voice channel with the user in.
            event.getGuild().getAudioManager().openAudioConnection(channelToJoin);
            logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          }
          break;
        }
        case "leave": {
          // Disconnect the audio connection with the VoiceChannel.
          event.getGuild().getAudioManager().closeAudioConnection();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "play": {
          // Plays audio with the URLPlayer
          String urlString = event.getOption("url").getAsString();
          loadAndPlay(event, urlString);

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "list": {
          event.deferReply().queue();

          GuildMusicManager musicMgr = getGuildAudioPlayer(event.getGuild());

          if (!musicMgr.isEmpty()) {
            logger.info("Playlist is not empty!");
            MessageHelper.sendPage("**Playlist: **", getTrackInfos(musicMgr.getQueue()), 1, 15,
                waiter, event.getChannel(), 1, TimeUnit.HOURS);

            event.getHook().sendMessage("Processed Command!").queue();
          } else {
            logger.info("Playlist is empty!");
            event.getHook().sendMessage("The Queue is empty!").queue();
          }

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "volume": {
          OptionMapping option = event.getOption("integer");
          if (option == null) {
            event.reply(
                String.format("**Current volume:** *%d* / 100", getVolume(event.getTextChannel())))
                .queue();
          } else {
            try {

              int temp = Math.toIntExact(option.getAsLong());

              // Sanitize user input.
              if (temp < 0) {
                temp = 0;
              } else if (temp > 100) {
                temp = 100;
              }

              setVolume(event.getTextChannel(), temp);

              event
                  .reply(
                      String.format("Setting the volume to %d", getVolume(event.getTextChannel())))
                  .queue();

              logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
            } catch (NumberFormatException e) {
              event.reply("You must input an int value between 0-100. (inclusive)").queue();

              logger.warn(LogHelper.elog(event, String.format("Incorrect Command: %s", message)));
            }
          }
          break;
        }
        case "resume": {
          getGuildAudioPlayer(event.getGuild()).getPlayer().setPaused(false);

          event.reply("Resumed Song!").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "pause": {
          getGuildAudioPlayer(event.getGuild()).getPlayer().setPaused(true);

          event.reply("Paused Song!").queue();

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "current": {
          AudioTrack track = getGuildAudioPlayer(event.getGuild()).getPlayer().getPlayingTrack();
          if (track != null) {
            event.reply(String.format("Current song: \"%s\" by %s", track.getInfo().title,
                track.getInfo().author)).queue();
          } else {
            event.reply("No songs playing!").queue();
          }

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "skip": {
          OptionMapping option = event.getOption("number");
          if (option == null) {
            // No number given. Defaulting to skipping 1 track.
            skipTrack(event, 1);
          } else {
            try {
              int num = Math.toIntExact(option.getAsLong());

              if (num > 0) {
                skipTrack(event, num);
                logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
              }
            } catch (ArithmeticException e) {
              logger.warn(LogHelper.elog(event, String.format("Incorrect Command: %s", message)));
            }
          }

          break;
        }
        case "skipall": {
          skipAllTrack(event);

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
        case "shuffle": {
          shufflePlaylist(event);

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        }
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("join", "Bot joins the voice channel you are in."));
    super.commands.add(new CommandData("leave", "Leaves a voice channel."));
    super.commands.add(new CommandData("play", "Plays an audio from a url.")
        .addOption(OptionType.STRING, "url", "The URL to play.", true));
    super.commands.add(new CommandData("list", "Displays the Playlist."));
    super.commands.add(new CommandData("volume", "Sets or displays the current volume.").addOption(
        OptionType.INTEGER, "integer",
        "A volume to be set with the numbers between (0-100) inclusive.", false));
    super.commands.add(new CommandData("resume", "Resumes the audio."));
    super.commands.add(new CommandData("pause", "Pauses the audio."));
    super.commands.add(new CommandData("current", "Displays the current song."));
    super.commands.add(new CommandData("skip", "Skips the current or set amount of tracks.")
        .addOption(OptionType.INTEGER, "number", "Number of tracks to skip.", false));
    super.commands.add(new CommandData("skipall", "Clears the Playlist."));
    super.commands.add(new CommandData("shuffle", "Shuffles the Playlist."));
  }
}
