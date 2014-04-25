package com.vijaysharma.ehyo.core.models;

public class Project {
	private final String name;
	private final GradleBuild build;

	public Project(String name, GradleBuild build) {
		this.name = name;
		this.build = build;
	}

	public GradleBuild getBuild() {
		return build;
	}
	
	public String getName() {
		return name;
	}
}
