package com.vijaysharma.ehyo.api;

import java.util.List;

public interface Template {
	List<TemplateFileParameter> loadTemplateParameters(ProjectSourceSet sourceSet);
	TemplateInfo loadTemplateInformation();
}
