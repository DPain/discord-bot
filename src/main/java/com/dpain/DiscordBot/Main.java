package com.dpain.DiscordBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private final static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Main Initialized!");

    DiscordBot myBot = new DiscordBot();
    myBot.readConsole();
  }
}
