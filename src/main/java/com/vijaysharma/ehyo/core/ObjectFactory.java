package com.vijaysharma.ehyo.core;

public class ObjectFactory {
	public <T> T create(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
}
