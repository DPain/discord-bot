package com.dpain.DiscordBot.exception;

public class NoPermissionException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4513883060550178236L;
	
	private String userName;
	
	public NoPermissionException() {
		super("User did not have enough permissions!");
		userName = "";
	}
	
	public NoPermissionException(String userName) {
		super("User did not have enough permissions! Username: " + userName);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
