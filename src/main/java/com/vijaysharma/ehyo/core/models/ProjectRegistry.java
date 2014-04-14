package com.vijaysharma.ehyo.core.models;

import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;

public class ProjectRegistry {
	private final Map<String, Project> projects;
	private final File root;

	public ProjectRegistry(File root, Map<String, Project> projects) {
		this.root = root;
		this.projects = projects;
	}

	public List<AndroidManifest> getAllAndroidManifests() {
		return getAllAndroidManifests(Functions.<AndroidManifest>identity());
	}
	
	public <T> List<T> getAllAndroidManifests(Function<AndroidManifest, T> transform) {
		ImmutableList.Builder<T> builds = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			builds.addAll(transform(project.getManifests(), transform));
		}
		
		return builds.build();
	}
	
	public List<GradleBuild> getAllGradleBuilds() {
		return getAllGradleBuilds(Functions.<GradleBuild>identity());
	}
	
	public <T> List<T> getAllGradleBuilds(Function<GradleBuild, T> transform) {
		ImmutableList.Builder<T> builds = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			builds.addAll(transform(project.getBuilds(), transform));
		}
		
		return builds.build();
	}

	public GradleBuild getGradleBuild(String id) {
		for ( GradleBuild build : getAllGradleBuilds() ) {
			if ( build.getId().equals(id) )
				return build;
		}
		
		throw new IllegalArgumentException("Uknown Gradle file: " + id);
	}
	
	public AndroidManifest getAndroidManifest(String id) {
		for ( AndroidManifest manifest : getAllAndroidManifests() ) {
			if ( manifest.getId().equals(id) )
				return manifest;
		}
		
		throw new IllegalArgumentException("Uknown Android Manifest file: " + id);
	}
}
