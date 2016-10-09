package com.dpain.DiscordBot.plugin.weather;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherData {
	public static final String JSON_WEATHER   = "weather";
	public static final String JSON_MAIN      = "main";
	public static final String JSON_WIND      = "wind";
	public static final String JSON_RAIN      = "rain";
	public static final String JSON_SNOW      = "snow";
	
	private Weather weather = null;
	private Main main = null;
	private Wind wind = null;
	private Rain rain = null;
	private Snow snow = null;
	
	public WeatherData(JSONObject json) {
		if(json.has(WeatherData.JSON_WEATHER)) {
			weather = new Weather(json.getJSONArray(WeatherData.JSON_WEATHER));
		}
		if(json.has(WeatherData.JSON_MAIN)) {
			main = new Main(json.optJSONObject(WeatherData.JSON_MAIN));
		}
		if(json.has(WeatherData.JSON_WIND)) {
			wind = new Wind(json.optJSONObject(WeatherData.JSON_WIND));
		}
		if(json.has(WeatherData.JSON_RAIN)) {
			rain = new Rain(json.optJSONObject(WeatherData.JSON_RAIN));
		}
		if(json.has(WeatherData.JSON_SNOW)) {
			snow = new Snow(json.optJSONObject(WeatherData.JSON_SNOW));
		}
	}
	
	public String getWeatherCondition(float id) {
		return WeatherCondition.getWeatherCondition((int) id);
	}
	
	public String getCommonDataToString() {
		String result = "";
		if(weather!=null) {
			result += "\n\tWeather Condition: " + getWeatherCondition(weather.getId());
		}
		if(main!=null) {
			result += "\n\tTemperature: " + (float)(((int)((main.getTemp() - 273.15f)*100))/100) + " 'C, Min: " + (float)(((int)((main.getTempMin() - 273.15f)*100))/100) +" 'C, Max: " + (float)(((int)((main.getTempMax() - 273.15f)*100))/100) + " 'C";
		}
		if(wind!=null && Float.isNaN(wind.getSpeed())) {
			result += "\n\tWind Speed: " + (wind.getSpeed()) + "m/s";
		}
		if(rain!=null && Float.isNaN(rain.getRain())) {
			result += "\n\tRain: " + rain.getRain() + "mm";
		}
		if(snow!=null && Float.isNaN(snow.getSnow())) {
			result += "\n\tSnow: " + snow.getSnow() + "mm";
		}
		result += "\n";
		return result;
	}
	
	public class Weather {
		private static final String ID = "id";
		
		private final float id;
		
		public Weather(JSONArray json) {
			id = (float) json.getJSONObject(0).optDouble(Weather.ID);
		}
		
		public float getId() {
			return id;
		}
	}
	
	public class Main {
		private static final String TEMP = "temp";
		private static final String TEMP_MIN = "temp_min";
		private static final String TEMP_MAX = "temp_max";
		private static final String PRESSURE = "pressure";
		private static final String HUMIDITY = "humidity";
		
		private final float temp;
		private final float temp_min;
		private final float temp_max;
		private final float pressure;
		private final float humidity;
		
		public Main(JSONObject json) {
			temp = (float) json.optDouble (Main.TEMP);
			temp_min = (float) json.optDouble (Main.TEMP_MIN);
			temp_max = (float) json.optDouble (Main.TEMP_MAX);
			pressure = (float) json.optDouble (Main.PRESSURE);
			humidity = (float) json.optDouble (Main.HUMIDITY);
		}
		
		public float getTemp() {
			return temp;
		}
		
		public float getTempMin() {
			return temp_min;
		}
		
		public float getTempMax() {
			return temp_max;
		}
		
		public float getPressure() {
			return pressure;
		}
		
		public float getHumidity() {
			return humidity;
		}
	}
	
	public class Wind {
		private static final String SPEED = "speed";
		private static final String DIRECTION = "deg";
		
		private final float speed;
		private final float direction;
		
		public Wind(JSONObject json) {
			speed = (float) json.optDouble (Wind.SPEED);
			direction = (float) json.optDouble (Wind.DIRECTION);
		}
		
		public float getSpeed() {
			return speed;
		}
		
		public float getDirection() {
			return direction;
		}
	}
	
	public class Rain {
		private static final String VOLUME3H = "1h";
		
		private final float volume1H;
		
		public Rain(JSONObject json) {
			volume1H = (float) json.optDouble(Rain.VOLUME3H);
		}
		
		public float getRain() {
			return volume1H;
		}
	}
	
	public class Snow {
		private static final String VOLUME3H = "3h";
		
		private final float volume3H;
		
		public Snow(JSONObject json) {
			volume3H = (float) json.optDouble (Snow.VOLUME3H);
		}
		
		public float getSnow() {
			return volume3H;
		}
	}
}
