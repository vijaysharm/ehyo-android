package com.vijaysharma.ehyo.api;

import java.util.List;

public interface ProjectSourceSet {
	void applyTemplate(Template template, List<TemplateParameters> parameters);
}
