package com.vijaysharma.ehyo.api;

import java.util.Map;

public interface TemplateFileParameter {
	public String getId();
	public String getName();
	public String getDefaultValue();
	public String getType();
	public String getHelp();
	public Map<String, String> getOptions();
}
