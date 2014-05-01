package com.vijaysharma.ehyo.api;


public class GentleMessageException extends RuntimeException {
	public GentleMessageException(String message) {
		super(message);
	}
	
	public GentleMessageException(String message, Throwable thrown) {
		super(message, thrown);
	}
}
