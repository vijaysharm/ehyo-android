package com.vijaysharma.ehyo.api;

import java.util.Set;

public interface BuildConfiguration {
	void addDependency(String projectId);
	Set<String> getDependencies();
}
