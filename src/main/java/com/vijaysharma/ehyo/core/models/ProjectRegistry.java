package com.vijaysharma.ehyo.core.models;

import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
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
	
	public <T> List<T> getAllAndroidManifests(Function<AndroidManifest, T> transform) {
		ImmutableList.Builder<T> builds = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			builds.addAll(transform(project.getManifests(), transform));
		}
		
		return builds.build();
	}
	
	public <T> List<T> getAllGradleBuilds(Function<GradleBuild, T> transform) {
		ImmutableList.Builder<T> builds = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			builds.addAll(transform(project.getBuilds(), transform));
		}
		
		return builds.build();
	}
}
