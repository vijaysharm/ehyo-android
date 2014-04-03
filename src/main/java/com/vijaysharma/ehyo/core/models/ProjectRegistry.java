package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

public class ProjectRegistry {
	private final Map<String, Project> projects;
	private final File root;

	public ProjectRegistry(File root, Map<String, Project> projects) {
		this.root = root;
		this.projects = projects;
	}
	
	public Map<String, Project> getProjects() {
		return projects;
	}

	public List<AndroidManifest> getAllAndroidManifests() {
		ImmutableList.Builder<AndroidManifest> manifests = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			manifests.addAll( project.getManifests() );
		}
		
		return manifests.build();
	}
}
