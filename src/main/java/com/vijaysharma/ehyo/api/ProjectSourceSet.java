package com.vijaysharma.ehyo.api;

import java.util.List;

public interface ProjectSourceSet {
	void applyTemplate(String templatePath, List<TemplateParameters> parameters);
}
