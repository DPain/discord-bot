package com.dpain.DiscordBot.plugin.weather;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherDataSet {
	private ArrayList<WeatherData> dataSet;

	private String city;
	
	public WeatherDataSet(String city, JSONObject json) {
		this.city = city;
		
		JSONArray list = json.getJSONArray("list");
		
		dataSet = new ArrayList<WeatherData>();
		for(int i = 0; i < list.length(); i++) {
			dataSet.add(new WeatherData(list.getJSONObject(i)));
		}
	}
	
	public String getCity() {
		return city;
	}
	
	public ArrayList<WeatherData> getDataSet() {
		return dataSet;
	}
}
