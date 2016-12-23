package com.dpain.DiscordBot.plugin.anime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeTorrentFinder {
	//Max message length is 2000
	public static int charLimit = 2000;
	public int entryLimit;
	
	public AnimeTorrentFinder() {
		entryLimit = 10;
	}
	
	public String getFullSchedule() {
		//@todo WIP
		return "";
	}
	
	public String getCurrentSchedule() {
		//@todo WIP
		return "";
	}
	
	public LinkedList<String> searchTorrent(String name) throws IOException {
		//@todo tokyo-toshokan is down. Change to nyaa.se
		
		LinkedList<String> torrentList = new LinkedList<String>();
		
		String parseLink = "http://tokyo-tosho.net/search.php?terms=";
		try {
			parseLink += URLEncoder.encode(name, "UTF-8") + "&type=0&size_min=&size_max=&username=";
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
		
		Document doc = Jsoup.connect(parseLink).get();
		
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
