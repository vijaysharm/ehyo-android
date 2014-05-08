package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;

public class GradleBuild implements HasDocument {
	private final File file;
	private final String id;
	private final Set<BuildType> buildTypes;
	private final Set<Flavor> flavors;
	private final Map<SourceSetType, SourceSet> sourceSets;
	private final Multimap<DependencyType, Dependency> dependencies;
	private final String project;
	
	public GradleBuild(String projectName, 
					   File file, 
					   Set<BuildType> buildTypes, 
					   Set<Flavor> flavors, 
					   Map<SourceSetType, SourceSet> sourceSets, 
					   Multimap<DependencyType, Dependency> dependencies) {
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
	
	public Set<Artifact> getArtifacts(DependencyType type) {
		ImmutableSet.Builder<Artifact> artifacts = ImmutableSet.builder(); 
		Collection<Dependency> deps = dependencies.get(type);
		for ( Dependency dep : deps ) {

			Artifact artifact = Artifact.read(dep.getLibrary());
			if ( artifact != null )
				artifacts.add(artifact);
		}
		
		return artifacts.build();
	}
	
	public Multimap<DependencyType, Dependency> getDependencies() {
		return this.dependencies;
	}
	
	public Set<BuildType> getBuildTypes() {
		return this.buildTypes;
	}

	public Set<Flavor> getFlavors() {
		return this.flavors;
	}
	
	public Map<SourceSetType, SourceSet> getSourceSets() {
		return sourceSets;
	}
	
	@Override
	public String toString() {
		return getProject() + ":" + getFile().getName();
	}
}
