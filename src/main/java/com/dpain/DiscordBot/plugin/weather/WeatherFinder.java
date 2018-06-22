package com.dpain.DiscordBot.plugin.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.system.PropertiesManager;

public class WeatherFinder {
	private final static Logger logger = Logger.getLogger(WeatherFinder.class.getName());

	private final String APPID_HEADER = "x-api-key";

	private String apiKey;
	private String baseApiUrl;
	private HttpClient httpClient;

	public WeatherFinder() {
		apiKey = PropertiesManager.load().getValue(Property.WEATHER_API_KEY);
		baseApiUrl = "http://api.openweathermap.org/data/2.5/";
		httpClient = HttpClientBuilder.create().build();
	}

	public WeatherDataSet getWeathersByCity(String city) {
		WeatherDataSet weatherDataSet = null;
		String subUrl;

		try {
			subUrl = String.format(Locale.ROOT, "forecast?q=%s&units=metric&mode=json&APPID=%s",
					URLEncoder.encode(city, "UTF-8"), apiKey);
			JSONObject response = doQuery(subUrl);
			weatherDataSet = new WeatherDataSet(city, response);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "City name contains a non-acceptable character!");
		} catch (JSONException e) {
			logger.log(Level.SEVERE, "Received data is not in a correct JSON format!");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString());
		}
		return weatherDataSet;
	}

	private JSONObject doQuery(String subUrl) throws JSONException, IOException {
		String responseBody = null;
		HttpGet httpget = new HttpGet(this.baseApiUrl + subUrl);
		if (this.apiKey != null) {
			httpget.addHeader(APPID_HEADER, apiKey);
		}

		HttpResponse response = httpClient.execute(httpget);
		InputStream contentStream = null;
		try {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine == null) {
				throw new IOException(String.format("Unable to get a response from OWM server"));
			}
			int statusCode = statusLine.getStatusCode();
			if (statusCode < 200 && statusCode >= 300) {
				throw new IOException(
						String.format("OWM server responded with status code %d: %s", statusCode, statusLine));
			}
			/* Read the response content */
			HttpEntity responseEntity = response.getEntity();
			contentStream = responseEntity.getContent();
			Reader isReader = new InputStreamReader(contentStream);
			int contentSize = (int) responseEntity.getContentLength();
			if (contentSize < 0)
				contentSize = 8 * 1024;
			StringWriter strWriter = new StringWriter(contentSize);
			char[] buffer = new char[8 * 1024];
			int n = 0;
			while ((n = isReader.read(buffer)) != -1) {
				strWriter.write(buffer, 0, n);
			}
			responseBody = strWriter.toString();
			contentStream.close();
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException re) {
			httpget.abort();
			throw re;
		} finally {
			if (contentStream != null)
				contentStream.close();
		}
		return new JSONObject(responseBody);
	}
}
