package com.vijaysharma.ehyo.core.utils;


public class GentleMessageException extends RuntimeException {
	public GentleMessageException(String message) {
		super(message);
	}
	
	public GentleMessageException(String message, Throwable thrown) {
		super(message, thrown);
	}
}
