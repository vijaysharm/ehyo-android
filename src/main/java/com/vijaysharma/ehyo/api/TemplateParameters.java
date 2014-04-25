package com.vijaysharma.ehyo.api;

public class TemplateParameters {
	private final String id;
	private final String defaultValue;
	private final String type;
	private final String name;

	public TemplateParameters(String id, 
							  String name,
							  String type,
							  String defaultValue,
							  String constraints,
							  String help) {
		this.id = id;
		this.type = type;
		this.defaultValue = defaultValue;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getDefaultValue() {
		if ( "boolean".equals(type) ) {
			return Boolean.parseBoolean(defaultValue);
		}

		return defaultValue;
	}
}
