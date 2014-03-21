package com.vijaysharma.ehyo.core.utils;

import java.io.IOException;

public class UncheckedIoException extends RuntimeException {
	public UncheckedIoException(IOException io) {
		super(io);
	}
}
