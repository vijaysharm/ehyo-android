package com.vijaysharma.ehyo.api;

import java.util.Set;

public interface ProjectBuild {
	Set<String> getFlavors();
	Set<String> getBuildTypes();
	Set<BuildConfiguration> getBuildConfigurations();
}
