package com.dpain.DiscordBot.plugin.reminder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

import com.dpain.DiscordBot.enums.Timezone;

public class Scheduler {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
	
	private Timer timer;
	
	/**
	 * Constructor
	 */
	public Scheduler() {
		timer = new Timer();
	}
	
	/**
	 * Constructor
	 * @param reminder The reminder
	 * @param time The time in hours to alert the reminder
	 */
	public Scheduler(Reminder reminder, double time) {
		timer = new Timer();
		addReminder(reminder, time);
	}
	
	/**
	 * Adds a reminder to the scheduler with a default EST timezone
	 * @param reminder
	 * @param time
	 */
	public void addReminder(Reminder reminder, double time) {
		addReminder(reminder, time, Timezone.EST);
	}
	
	/**
	 * Adds a reminder to the scheduler with a specific timezone
	 * @param reminder
	 * @param time
	 * @param timezone
	 */
	public void addReminder(Reminder reminder, double time, Timezone timezone) {		
		int timeSec = hoursToSeconds(time);
		
		timer.schedule(reminder, timeSec * 1000);
	}
	
	public static int hoursToSeconds(double time) {
		int hours = (int) time;
		int minutes = (int) (time * 60) % 60;
		int seconds = (int) (time * (60*60)) % 60;
		
		int result = (hours * 3600) + (minutes * 60) + seconds;
		
		return result;
	}
	
	public static String getTimesFromNow(int seconds) {
		String result = "";
		
		ZonedDateTime time = ZonedDateTime.now();
		time.plusSeconds(seconds);

		for(Timezone zone : Timezone.class.getEnumConstants()) {
			result += String.format("\n%s %s", time.withZoneSameInstant(zone.getZoneId()).format(formatter), zone.getZoneId().toString());
		}
		return result;
	}
	
	public static String getTimeFromNow(int seconds, Timezone timezone) {
		ZonedDateTime time = ZonedDateTime.now();
		time.plusSeconds(seconds);

		String result = String.format("\n%s %s", time.withZoneSameInstant(timezone.getZoneId()).format(formatter), timezone.getZoneId().toString());
		return result;
	}
}
