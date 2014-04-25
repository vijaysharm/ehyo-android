package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;
import java.util.Set;

public class GradleBuild implements HasDocument {
	private final File file;
	private final String id;
	private final Set<BuildType> buildTypes;
	private final Set<Flavor> flavors;
	private final Set<SourceSet> sourceSets;
	private final List<Dependency> dependencies;
	private final String project;
	
	public GradleBuild(String projectName, File file, Set<BuildType> buildTypes, Set<Flavor> flavors, Set<SourceSet> sourceSets, List<Dependency> dependencies) {
		this.project = projectName;
		this.file = file;
		this.buildTypes = buildTypes;
		this.flavors = flavors;
		this.sourceSets = sourceSets;
		this.dependencies = dependencies;
		this.id = file.getAbsolutePath();
	}

	public String getProject() {
		return project;
	}

	@Override
	public File getFile() {
		return this.file;
	}
	
	public String getId() {
		return id;
	}
	
	public List<Dependency> getDependencies() {
		return this.dependencies;
	}
	
	public Set<BuildType> getBuildTypes() {
		return this.buildTypes;
	}

	public Set<Flavor> getFlavors() {
		return this.flavors;
	}
	
	public Set<SourceSet> getSourceSets() {
		return sourceSets;
	}
	
	@Override
	public String toString() {
		return getProject() + ":" + getFile().getName();
	}
}
