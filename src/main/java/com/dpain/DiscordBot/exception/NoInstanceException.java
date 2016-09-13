package com.dpain.DiscordBot.exception;

public class NoInstanceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1820196068629041675L;

	public NoInstanceException() {
		super("There were no Player instances instantiated!");
	}
}
