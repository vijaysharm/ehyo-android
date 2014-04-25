package com.vijaysharma.ehyo.api;

import java.util.List;

public interface BuildConfiguration {
	void addDependency(String projectId);
	void applyTemplate(String templatePath, List<TemplateParameters> parameters);
}
