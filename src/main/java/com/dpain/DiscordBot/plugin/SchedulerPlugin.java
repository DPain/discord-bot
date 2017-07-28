package com.dpain.DiscordBot.plugin;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Timezone;
import com.dpain.DiscordBot.plugin.reminder.Reminder;
import com.dpain.DiscordBot.plugin.reminder.Scheduler;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class SchedulerPlugin extends Plugin {
	private Scheduler scheduler;
	
	public SchedulerPlugin() {
		super("SchedulerPlugin", Group.TRUSTED_USER);
		scheduler = new Scheduler();
		
		super.helpString = "**Scheduler Plugin Usage:** \n"
				+ "-remind/알림 *\"hours later\"* *\"description\"* : Sets a reminder for x hours later.\n"
				+ "-time/시간 *\"hours later\"* : Gets the time x hours later for PST,CST,EST,KST.\n";
		EssentialsPlugin.appendHelpString(super.helpString);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof GuildMessageReceivedEvent) {
			try {
				GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
				String message = castedEvent.getMessage().getContent();
		        
				if((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) || canAccessPlugin(castedEvent.getMember())) {
					
					if(message.startsWith("-")) {
		                if(message.startsWith("-remind ")) {
		                	String param = message.substring(8);
		                	try {
		                		int indexOfFirstSpace = param.indexOf(" ");
		                		double hours = Double.parseDouble(param.substring(0, indexOfFirstSpace));
		                		String description = param.substring(indexOfFirstSpace + 1);
	                			scheduler.addReminder(new Reminder(castedEvent.getAuthor().getAsMention(), castedEvent.getChannel(), param.substring(indexOfFirstSpace + 1)), hours);
	                			
	                			castedEvent.getChannel().sendMessage("Reminder set " + hours + " hours later for: " + description);
							} catch(Exception e) {
								castedEvent.getChannel().sendMessage("Please input a correct time in hours!");
							}
		                } else if(message.startsWith("-알림 ")) {
		                	String param = message.substring(4);
		                	try {
		                		int indexOfFirstSpace = param.indexOf(" ");
		                		double hours = Double.parseDouble(param.substring(0, indexOfFirstSpace));
		                		String description = param.substring(indexOfFirstSpace + 1);
	                			scheduler.addReminder(new Reminder(castedEvent.getAuthor().getAsMention(), castedEvent.getChannel(), param.substring(indexOfFirstSpace + 1)), hours);
	                			
	                			castedEvent.getChannel().sendMessage("" + hours + " 시간 뒤 알림이 설정되었습니다: " + description);
							} catch (Exception e) {
								castedEvent.getChannel().sendMessage("명령어를 제대로 쓰시기 바랍니다!");
							}  	
		                } else if(message.startsWith("-time ")) {
		                	String param = message.substring(6);
		                	double hours = Double.parseDouble(param.substring(0));
		                	
		                	String result = "";
                			for(Timezone zone : Timezone.class.getEnumConstants()) {
                				result += ("\n" + (Scheduler.getTimeFromNow(Scheduler.hoursToSeconds(hours), zone)));
                			}
                			castedEvent.getChannel().sendMessage("Time for each timezone:" + result);
		                } else if(message.startsWith("-시간 ")) {
		                	String param = message.substring(4);
		                	double hours = Double.parseDouble(param.substring(0));
		                	
		                	String result = "";
                			for(Timezone zone : Timezone.class.getEnumConstants()) {
                				result += ("\n" + (Scheduler.getTimeFromNow(Scheduler.hoursToSeconds(hours), zone)));
                			}
                			castedEvent.getChannel().sendMessage("타임존별 시간:" + result);
		                }
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
