package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Map;

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
}
