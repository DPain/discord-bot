package com.dpain.DiscordBot.enums;

import java.time.ZoneId;

public enum Timezone {
	// Timezones
	PST(ZoneId.of("America/Los_Angeles")),
	CST(ZoneId.of("America/Chicago")),
	EST(ZoneId.of("America/New_York")),
	KST(ZoneId.of("Asia/Seoul")),
	UTC(ZoneId.of("UTC")),
	ACT(ZoneId.of("Australia/Sydney"));
	
	private ZoneId id;
	
	Timezone(ZoneId id) {
		this.id = id;
	}
	
	public ZoneId getZoneId() {
        return id;
    }
	
}
