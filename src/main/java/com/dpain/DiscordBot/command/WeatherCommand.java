package com.dpain.DiscordBot.command;

import com.dpain.DiscordBot.command.weather.WeatherDataSet;
import com.dpain.DiscordBot.command.weather.WeatherFinder;
import com.dpain.DiscordBot.enums.Group;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

public class WeatherCommand extends Command {

	public WeatherCommand() {
		super("WeatherCommand", Group.USER);
		super.helpString = "**Weather Command Usage:** \n-weather : Gets the weather at the University of Delaware.\n";
		EssentialsCommand.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if(canAccessCommand(castedEvent.getAuthor()) && !castedEvent.getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
					
					if(message.startsWith("-")) {
		                
		                if(message.equals("-weather")) {
		                	//Gets the weather data of Newark, DE
		                	
		                	/**
		                	 * @TODO Fix weather command
		                	 */
		                	
		                	WeatherFinder weatherFinder = new WeatherFinder();
		                	WeatherDataSet weatherDataSet = weatherFinder.getWeathersByCity("Newark");
		                	
		                	String msg = "***" + weatherDataSet.getCity() + "'s*** **Weather Forecast:** \n";
		                	
		                	for(int i = 0; i < weatherDataSet.getDataSet().size(); i++) {
		                		if(i == 0) {
		                			msg += "\t*Today*";
		                		} else if(i == 1) {
		                			msg += "\t*Tomorrow*";
		                		} else if(i == 2) {
		                			msg += "\t*Two Days Later*";
		                		} else if(i == 3) {
		                			msg += "\t*Three Days Later*";
		                		} else {
		                			msg += "\t*Day " + i + "*";
		                		}
		                		
		                		msg += weatherDataSet.getDataSet().get(i).getCommonDataToString();
		                	}
		                	
		                	castedEvent.getChannel().sendMessage(msg);
		                }
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
