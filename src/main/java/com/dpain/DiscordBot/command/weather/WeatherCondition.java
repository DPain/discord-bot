package com.dpain.DiscordBot.command.weather;

import java.util.HashMap;

public class WeatherCondition {
	public static String getWeatherCondition(int key) {
		HashMap<Integer, String> conditions = new HashMap<Integer, String>();
		conditions.put(200, "Thunderstorm with light rain");
		conditions.put(201, "Thunderstorm with rain");
		conditions.put(202, "Thunderstorm with heavy rain");
		conditions.put(210, "Light thunderstorm");
		conditions.put(211, "Thunderstorm");
		conditions.put(212, "Heavy thunderstorm");
		conditions.put(221, "Ragged thunderstorm");
		conditions.put(230, "Thunderstorm with light drizzle");
		conditions.put(231, "Thunderstorm with drizzle");
		conditions.put(232, "Thunderstorm with heavy drizzle");
		conditions.put(300, "Light intensity drizzle");
		conditions.put(301, "drizzle");
		conditions.put(302, "Heavy intensity drizzle");
		conditions.put(310, "Light intensity drizzle rain");
		conditions.put(311, "Drizzle rain");
		conditions.put(312, "Heavy intensity drizzle rain");
		conditions.put(321, "Shower drizzle");
		conditions.put(500, "Light rain");
		conditions.put(501, "Moderate rain");
		conditions.put(502, "Heavy intensity rain");
		conditions.put(503, "Very heavy rain");
		conditions.put(504, "Extreme rain");
		conditions.put(511, "Freezing rain");
		conditions.put(520, "Light intensity shower rain");
		conditions.put(521, "Shower rain");
		conditions.put(522, "Heavy intensity shower rain");
		conditions.put(600, "Light snow");
		conditions.put(601, "Snow");
		conditions.put(602, "Heavy snow");
		conditions.put(611, "Sleet");
		conditions.put(621, "Shower snow");
		conditions.put(701, "Mist");
		conditions.put(711, "Smoke");
		conditions.put(721, "Haze");
		conditions.put(731, "Sand or dust whirls");
		conditions.put(741, "Fog");
		conditions.put(800, "Sky is clear");
		conditions.put(801, "Few clouds");
		conditions.put(802, "Scattered clouds");
		conditions.put(803, "Broken clouds");
		conditions.put(804, "Overcast clouds");
		conditions.put(900, "Tornado");
		conditions.put(901, "Tropical storm");
		conditions.put(902, "Hurricane");
		conditions.put(903, "Cold");
		conditions.put(904, "Hot");
		conditions.put(905, "Windy");
		conditions.put(906, "Hail");
		
		return (String) conditions.get(key);
	}
}
