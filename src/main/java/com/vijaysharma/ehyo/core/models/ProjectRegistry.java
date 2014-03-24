package com.vijaysharma.ehyo.core.models;

import java.util.Map;

public class ProjectRegistry {
	private final Map<String, Project> projects;
	public ProjectRegistry(Map<String,Project> projects) {
		this.projects = projects;
	}
}
