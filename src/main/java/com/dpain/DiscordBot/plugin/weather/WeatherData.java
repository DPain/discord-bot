package com.dpain.DiscordBot.plugin.weather;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.json.JSONObject;

public class WeatherData {
	public static final String JSON_DT   = "dt";
	public static final String JSON_WEATHER   = "weather";
	public static final String JSON_MAIN      = "main";
	public static final String JSON_TEMP  	  = "temp";
	public static final String JSON_WIND      = "wind";
	public static final String JSON_RAIN      = "rain";
	public static final String JSON_SNOW      = "snow";
	
	public ZonedDateTime date;
	public String weather;
	public Double temp;
	public Double rain;
	public Double snow;
	
	public WeatherData(JSONObject json) {
		if(json.has(WeatherData.JSON_DT)) {
			Instant instant = Instant.ofEpochSecond(json.optLong(WeatherData.JSON_DT));
			date = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		if(json.has(WeatherData.JSON_WEATHER)) {
			weather = json.optJSONArray(WeatherData.JSON_WEATHER).optJSONObject(0).optString("description");
		}
		if(json.has(WeatherData.JSON_MAIN)) {
			JSONObject main = json.optJSONObject(WeatherData.JSON_MAIN);
			if(main != null) {
				temp = main.optDouble(WeatherData.JSON_TEMP);
			}
		}
		if(json.has(WeatherData.JSON_RAIN)) {
			rain = json.optJSONObject(WeatherData.JSON_RAIN).optDouble("3h");
		}
		if(json.has(WeatherData.JSON_SNOW)) {
			snow = json.optJSONObject(WeatherData.JSON_SNOW).optDouble("3h");
		}
	}
	
	public String getCommonDataToString() {
		String result = "";
		if(date != null) {
			result += String.format("\n\tTime: %s", date.toString());
		}
		if(weather != null) {
			result += String.format("\n\tCondition: %s", weather);
		}
		if(temp != null) {
			result += String.format("\n\tTemp: %.1f'C", temp);
		}
		if(rain != null) {
			result += String.format("\n\tRain: %.2fmm (3hr)", rain);
		}
		if(snow != null) {
			result += String.format("\n\tSnow: %.2fmm (3hr)", snow);
		}
		result += "\n";
		return result;
	}
}
