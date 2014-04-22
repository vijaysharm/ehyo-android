package com.vijaysharma.ehyo.api;

public class TemplateParameters {
	private final String id;
	private final String defaultValue;
	private final String type;

	public TemplateParameters(String id, String type, String defaultValue) {
		this.id = id;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public String getId() {
		return id;
	}
	
	public Object getDefaultValue() {
		if ( "boolean".equals(type) ) {
			return Boolean.parseBoolean(defaultValue);
		}

		return defaultValue;
	}
}
