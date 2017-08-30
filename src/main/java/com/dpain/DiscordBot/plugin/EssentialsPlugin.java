	package com.dpain.DiscordBot.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.UserEventListener;
import com.dpain.DiscordBot.plugin.mcsplash.MinecraftSplashReader;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class EssentialsPlugin extends Plugin {
	private final static Logger logger = Logger.getLogger(EssentialsPlugin.class.getName());

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
		        
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) || canAccessPlugin(castedEvent.getMember())) {
					if(message.startsWith("-")) {
		                if(message.equals("-splash")) {
		        			//Easter Egg
		        			castedEvent.getChannel().sendMessage(mcSplash.getRandomSplash()).queue();
		        			logger.log(Level.INFO, LogHelper.elog(castedEvent, "User triggered the easter egg."));
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
		                		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                	} else {
		                		castedEvent.getChannel().sendMessage("**Twitch Emotes:** \n*" + twitchEmoteList + "*").queue();
		                		logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                	}
		                } else if(message.equals("-help")) {
		                	castedEvent.getChannel().sendMessage(EssentialsPlugin.helpString).queue();
		                	logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Command: %s", message)));
		                }
					} else {
						if(!castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
							// Processes the message to see if there are any emotes to display 
							
							int kappaNum = getNumberOfUniqueKappa(message);
							int peteZarollNum = getNumberOfUniquePeteZaroll(message);
							
							outerFor:
							for(String key : emoteMap.keySet()) {
								if(key.equals("Kappa".toLowerCase())) {
									if(kappaNum < 1) {
										continue outerFor;
									}
								}
								if(key.equals("PeteZaroll".toLowerCase())) {
									if(peteZarollNum < 1) {
										continue outerFor;
									}
								}
								if(message.toLowerCase().contains(key)) {
									castedEvent.getChannel().sendFile(emoteMap.get(key), null).queue();
									logger.log(Level.INFO, LogHelper.elog(castedEvent, String.format("Triggered emote: %s", key)));
								}
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
		str = str.toLowerCase();
		for(int i = 0; i < str.length(); i++) {
			String temp = str.substring(i);
			if((0 == temp.indexOf("Kappa".toLowerCase())) && (0 != temp.indexOf("KappaCool".toLowerCase())) && (0 != temp.indexOf("KappaClaus".toLowerCase())) && (0 != temp.indexOf("KappaHD".toLowerCase())) && (0 != temp.indexOf("KappaPride".toLowerCase())) && (0 != temp.indexOf("KappaRoss".toLowerCase())) && (0 != temp.indexOf("KappaWealth".toLowerCase())) && (0 != temp.indexOf("Blackappa".toLowerCase()))) {
				result++;
			}
		}
		return result;
	}
	
	private int getNumberOfUniquePeteZaroll(String str) {
		int result = 0;
		str = str.toLowerCase();
		for(int i = 0; i < str.length(); i++) {
			String temp = str.substring(i);
			if((0 == temp.indexOf("PeteZaroll".toLowerCase())) && (0 != temp.indexOf("PeteZarollTie".toLowerCase()))) {
				result++;
			}
		}
		return result;
	}
}
