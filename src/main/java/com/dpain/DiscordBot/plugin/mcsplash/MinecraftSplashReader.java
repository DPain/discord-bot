package com.dpain.DiscordBot.plugin.mcsplash;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MinecraftSplashReader
 * 
 * @author DPain
 *
 */
public class MinecraftSplashReader {
  private final static Logger logger = LoggerFactory.getLogger(MinecraftSplashReader.class);

  private ArrayList<String> splashes;
  private Random ran;

  /**
   * Constructor
   * 
   * @param Splashes file
   */
  public MinecraftSplashReader(File file) {
    splashes = new ArrayList<String>();
    ran = new Random();

    File splashFile = file;

    try {
      Scanner in = new Scanner(splashFile);
      while (in.hasNextLine()) {
        splashes.add(in.nextLine());
      }
      in.close();
    } catch (FileNotFoundException e) {
      splashes.add("Easter is over :(");
      logger.error("Splashes file does not exist! Using an fallback instead!");
    }
  }

  /**
   * Gets a random string.
   * 
   * @return random string
   */
  public String getRandomSplash() {
    return splashes.get(ran.nextInt(splashes.size()));
  }
}
