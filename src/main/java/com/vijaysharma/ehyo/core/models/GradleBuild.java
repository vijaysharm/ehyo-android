package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;

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
	
	public Set<Artifact> getArtifacts(BuildType type, Flavor flavor) {
		ImmutableSet.Builder<Artifact> artifacts = ImmutableSet.builder(); 
		for ( Dependency dependency : dependencies ) {
			if ( dependency.getBuildType().equals(type) ) {
				if ( dependency.getFlavor() == null && flavor == null ) {
					artifacts.add(dependency.getArtifact());
				} else if ( dependency.getFlavor() != null && dependency.getFlavor().equals(flavor)) {
					artifacts.add(dependency.getArtifact());
				} else if ( flavor != null && flavor.equals(dependency.getFlavor())) {
					artifacts.add(dependency.getArtifact());
				}
			}
		}
		
		return artifacts.build();
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
