package com.vijaysharma.ehyo.api;

import java.util.Set;

public interface ProjectBuild {
	Set<Flavor> getFlavors();
	Set<BuildType> getBuildTypes();
	Set<BuildConfiguration> getBuildConfigurations();
	Set<ProjectManifest> getManifests();
	Set<ProjectSourceSet> getSourceSets();
}
