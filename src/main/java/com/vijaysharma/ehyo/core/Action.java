package com.vijaysharma.ehyo.core;

public interface Action extends Runnable {
	public static final Action NULL = new Action() {
		@Override public void run() {}
	};
}
