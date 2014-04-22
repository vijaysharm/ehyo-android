package com.vijaysharma.ehyo.api;

import java.util.List;
import java.util.Set;

public interface BuildConfiguration {
	void addDependency(String projectId);
	Set<String> getDependencies();
	void applyTemplate(String templatePath, List<TemplateParameters> parameters);
}
