package com.dpain.DiscordBot;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.listener.g2g.G2gAlerter;

public class Main {

  private final static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Main Initialized!");

    DiscordBot myBot = new DiscordBot();
    myBot.readConsole();
  }
}
