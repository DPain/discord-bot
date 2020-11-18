package com.dpain.DiscordBot.listener;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class ConsoleInputReader implements Runnable {
  private final static Logger logger = LoggerFactory.getLogger(ConsoleInputReader.class);

  private JDA jda;
  private Guild processingGuild;
  private Scanner in;

  public ConsoleInputReader(JDA jda, PluginListener listener, Guild guild) {
    this.jda = jda;

    processingGuild = guild;
    in = new Scanner(System.in);

    logger.info("Reading input from the console!");
  }

  private boolean processConsoleCommand(String commandLine) {
    if (!commandLine.isEmpty()) {
      if (commandLine.equals("-exit")) {
        jda.shutdownNow();
        System.exit(0);
        return false;
      } else if (commandLine.startsWith("-changeguild ")) {
        String guildId = commandLine.substring(13);
        changeGuildById(guildId);
      } else if (commandLine.equals("-help")) {
        logger.info("Commands:\n" + "-exit = Terminates the bot\n"
            + "-changeguild [guild id] = Changes the guild the bot will forward the commands\n");
      } else {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append(commandLine);
        processingGuild.getDefaultChannel().sendMessage(messageBuilder.build());
      }
    }
    return true;
  }

  private void changeGuildById(String id) {
    processingGuild = jda.getGuildById(id);
  }

  public void run() {
    // Might have to fix
    outerWhile: while (true) {
      logger.debug("Enter Command: ");
      String commandLine = in.nextLine();
      if (!processConsoleCommand(commandLine)) {
        break outerWhile;
      }
    }
  }
}
