package com.dpain.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private final static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Main Initialized!");

    // Initial folder setup    
    Path rscDir = Paths.get("rsc");
    if (!Files.isDirectory(rscDir)) {
      try {
        Files.createDirectories(rscDir);
      } catch (IOException e) {
        // Fail to create directory
        e.printStackTrace();
      }
    }

    DiscordBot myBot = new DiscordBot();
    myBot.readConsole();
  }
}
