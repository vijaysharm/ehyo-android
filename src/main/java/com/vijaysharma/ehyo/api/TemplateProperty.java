package com.vijaysharma.ehyo.api;

public class TemplateProperty {
	private final String id;
	private final String value;
	private final String type;

	public TemplateProperty(String id, String value, String type) {
		this.id = id;
		this.value = value;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public Object getValue() {
		if ( "boolean".equals(type) ) {
			return Boolean.parseBoolean(value);
		}

		return value;
	}
}
