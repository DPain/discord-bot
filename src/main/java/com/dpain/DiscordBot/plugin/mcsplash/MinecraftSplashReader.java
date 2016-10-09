package com.dpain.DiscordBot.plugin.mcsplash;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MinecraftSplashReader {
	ArrayList<String> splashes;
	Random ran;
	
	public MinecraftSplashReader(String fileDir) {
		splashes = new ArrayList<String>();
		ran = new Random();
		
		File splashFile = new File(fileDir);
		
		try {
			Scanner in = new Scanner(splashFile);
			while(in.hasNextLine()) {
				splashes.add(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getRandomSplash() {
		return splashes.get(ran.nextInt(splashes.size()));
	}
}
