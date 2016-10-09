	package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.util.HashMap;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.plugin.mcsplash.MinecraftSplashReader;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class EssentialsPlugin extends Plugin {
	private HashMap<String, File> emoteMap;
	private MinecraftSplashReader mcSplash;
	private String twitchEmoteList;
	
	private static String helpString = "";
	
	public static void appendHelpString(String help) {
		helpString += help;
	}
	
	public EssentialsPlugin() {
		super("EssentialsPlugin", Group.TRUSTED_USER);
		
		emoteMap = new HashMap<String, File>();
		instantiateEmoteMap();
		mcSplash = new MinecraftSplashReader("./rsc/splashes.txt");
		
		String temp = "**Essentials Plugin Usage:** \n"
				+ "-splash : Gets a random string.\n"
				+ "-emotes : Returns the list of twitch emotes available.\n"
				+ "-help : Displays the available commands.\n";
		
		appendHelpString(temp);
	}
	
	private void instantiateEmoteMap() {
		File imageFolder = new File("./rsc/img");
		File[] listOfFiles = imageFolder.listFiles();
		
		String temp = "";
		
		//Puts all the emotes that are in the ./rsc/img directory into the HashMap
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  String fileName = listOfFiles[i].getName().substring(0,listOfFiles[i].getName().indexOf("."));
	    	  String key = fileName.replace("(C)", ":");
	    	  emoteMap.put(key.toLowerCase(), new File("./rsc/img/" + fileName + ".png"));
	    	  temp += key + ", ";
	      }
	    }
	    temp = temp.substring(0, temp.length() - 2);
	    twitchEmoteList = temp;
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessPlugin(castedEvent.getAuthor()) && !castedEvent.getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
					
					if(message.startsWith("-")) {
		                
		                if(message.equals("-splash")) {
		        			//Easter Egg
		        			castedEvent.getChannel().sendMessage(mcSplash.getRandomSplash());
		        			
		                } else if(message.equals("-emotes")) {
		                	/**
		                	 * @TODO Refine message format to not have commands to be splitted
		                	 */
		                	if(twitchEmoteList.length() > 1800) {
		                		int numRecurssion = twitchEmoteList.length() / 1800;
		                		// Safe margin of 1800 instead of 2000
		                		for(int i = 0; i <= numRecurssion; i++) {
			                		if(i == 0) {
			                			String temp = twitchEmoteList.substring(i * 1800, (1 + i) * 1800);
			                			castedEvent.getChannel().sendMessage("**Twitch Emotes:** \n*" + temp + "*\n (" + (i + 1) + "/" + (numRecurssion + 1) + ")");
			                		} else if(i >= numRecurssion) {
			                			String temp = twitchEmoteList.substring(i * 1800);
			                			castedEvent.getChannel().sendMessage("*" + temp + "*\n (" + (i + 1) + "/" + (numRecurssion + 1) + ")");
			                		} else {
			                			String temp = twitchEmoteList.substring(i * 1800, (1 + i) * 1800);
			                			castedEvent.getChannel().sendMessage("*" + temp + "*\n (" + (i + 1) + "/" + (numRecurssion + 1) + ")");
			                		}
			                	}
		                	} else {
		                		castedEvent.getChannel().sendMessage("**Twitch Emotes:** \n*" + twitchEmoteList + "*");
		                	}
		                } else if(message.equals("-help")) {
		                	castedEvent.getChannel().sendMessage(EssentialsPlugin.helpString);
		                }
					} else {
						// Processes the message too see if there are any emotes to display 
						
						int kappaNum = getNumberOfUniqueKappa(message);
						int peteZarollNum = getNumberOfUniquePeteZaroll(message);
						
						outerFor:
						for(String key : emoteMap.keySet()) {
							if(key.equals("Kappa")) {
								if(kappaNum < 1) {
									continue outerFor;
								}
							}
							if(key.equals("PeteZaroll")) {
								if(peteZarollNum < 1) {
									continue outerFor;
								}
							}
							if(message.toLowerCase().contains(key)) {
								System.out.println("Existing Key: " + key);
								castedEvent.getChannel().sendFile(emoteMap.get(key), null);
							}
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getNumberOfUniqueKappa(String str) {
		int result = 0;
		for(int i = 0; i < str.length(); i++) {
			String temp = str.substring(i);
			if((0 == temp.indexOf("Kappa")) && (0 != temp.indexOf("KappaCool")) && (0 != temp.indexOf("KappaClaus")) && (0 != temp.indexOf("KappaHD")) && (0 != temp.indexOf("KappaPride")) && (0 != temp.indexOf("KappaRoss")) && (0 != temp.indexOf("KappaWealth")) && (0 != temp.indexOf("Blackappa"))) {
				result++;
			}
		}
		return result;
	}
	
	private int getNumberOfUniquePeteZaroll(String str) {
		int result = 0;
		for(int i = 0; i < str.length(); i++) {
			String temp = str.substring(i);
			if((0 == temp.indexOf("PeteZaroll")) && (0 != temp.indexOf("PeteZarollTie"))) {
				result++;
			}
		}
		return result;
	}
}
