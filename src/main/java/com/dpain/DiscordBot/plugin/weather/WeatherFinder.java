package com.dpain.DiscordBot.plugin.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherFinder {
	private final String APPID_HEADER = "x-api-key";
	
	private String apiKey;
	private String baseApiUrl;
	private HttpClient httpClient;
	
	public WeatherFinder() {
		apiKey = "36121a11f9008d31281b2e0a657f6626";
		baseApiUrl = "http://api.openweathermap.org/data/2.1/";
		httpClient = HttpClientBuilder.create().build();
	}
	
	public WeatherDataSet getWeathersByCity(String city) {
		WeatherDataSet weatherDataSet = null;
		String subUrl = String.format (Locale.ROOT, "find/name?q=%s", city);
		JSONObject response;
		
		try {
			response = doQuery (subUrl);
			weatherDataSet = new WeatherDataSet(city, response);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return weatherDataSet;
	}
	
	private JSONObject doQuery (String subUrl) throws JSONException, IOException {
		String responseBody = null;
		HttpGet httpget = new HttpGet (this.baseApiUrl + subUrl);
		if (this.apiKey != null) {
			httpget.addHeader (APPID_HEADER, apiKey);
		}

		HttpResponse response = httpClient.execute (httpget);
		InputStream contentStream = null;
		try {
			StatusLine statusLine = response.getStatusLine ();
			if (statusLine == null) {
				throw new IOException (
						String.format ("Unable to get a response from OWM server"));
			}
			int statusCode = statusLine.getStatusCode ();
			if (statusCode < 200 && statusCode >= 300) {
				throw new IOException (
						String.format ("OWM server responded with status code %d: %s", statusCode, statusLine));
			}
			/* Read the response content */
			HttpEntity responseEntity = response.getEntity ();
			contentStream = responseEntity.getContent ();
			Reader isReader = new InputStreamReader (contentStream);
			int contentSize = (int) responseEntity.getContentLength ();
			if (contentSize < 0)
				contentSize = 8*1024;
			StringWriter strWriter = new StringWriter (contentSize);
			char[] buffer = new char[8*1024];
			int n = 0;
			while ((n = isReader.read(buffer)) != -1) {
					strWriter.write(buffer, 0, n);
			}
			responseBody = strWriter.toString ();
			contentStream.close ();
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException re) {
			httpget.abort ();
			throw re;
		} finally {
			if (contentStream != null)
				contentStream.close ();
		}
		return new JSONObject (responseBody);
	}
}
