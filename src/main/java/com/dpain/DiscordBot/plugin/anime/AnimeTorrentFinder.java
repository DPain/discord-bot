package com.dpain.DiscordBot.plugin.anime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.plugin.WikipediaPlugin;
import com.dpain.DiscordBot.system.PropertiesManager;

public class AnimeTorrentFinder {
	private final static Logger logger = Logger.getLogger(AnimeTorrentFinder.class.getName());
	
	//Max message length is 2000
	public static int charLimit = 2000;
	public int entryLimit;
	
	/**
	 * Constructor
	 */
	public AnimeTorrentFinder() {
		try {
			entryLimit = Integer.parseInt(PropertiesManager.load().getValue(Property.TORRENT_ENTRY_LIMIT));
		} catch(NumberFormatException e) {
			logger.log(Level.SEVERE, String.format("Property: %s is not formatted correctly!", Property.TORRENT_ENTRY_LIMIT.getKey()));
			System.exit(1);
		}
		
	}
	
	public String getFullSchedule() {
		//TODO WIP
		return "";
	}
	
	public String getCurrentSchedule() {
		//TODO WIP
		return "";
	}
	
	/**
	 * Search torrent files from tokyotosho.info. Works as of 6/22/2018.
	 * @param name Search parameter.
	 * @return LinkedList<String> List of strings which are sent by the bot each time.
	 * @throws IOException
	 */
	public LinkedList<String> searchTorrent(String name) throws IOException {
		LinkedList<String> torrentList = new LinkedList<String>();
		
		String parseLink = "https://www.tokyotosho.info/search.php?terms=";
		try {
			parseLink += URLEncoder.encode(name, "UTF-8") + "&type=7&size_min=&size_max=&username=";
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		
		// Necessary due to tokyo-toshokan's Cloudfare implementation.
		Connection connection = Jsoup.connect(parseLink).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		
		Document doc = connection.get();
		
		String charLimitBound = "";
		Elements info = doc.select("td.desc-top");
		for(int i = 0; i < info.size() && i < entryLimit; i++) {
			Elements torrents = info.get(i).select("a[type=application/x-bittorrent]");
			for(Element element : torrents) {
				String temp = "Entry " + (i + 1) + "\n\tName: " + element.text() + "\n\tLink: " + element.attr("href") + "\n";
				if(charLimitBound.length() + temp.length() >= charLimit) {
					torrentList.add(charLimitBound);
					charLimitBound = temp;
				} else {
					charLimitBound += temp;
				}
			}
		}
		torrentList.add(charLimitBound);
		
		return torrentList;
	}
}
