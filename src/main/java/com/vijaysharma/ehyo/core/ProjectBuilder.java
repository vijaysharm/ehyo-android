package com.vijaysharma.ehyo.core;

import java.io.File;

import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.Project;

public class ProjectBuilder {
	private final ImmutableMap.Builder<String, AndroidManifest> manifests = ImmutableMap.builder();
	private final ImmutableMap.Builder<String, GradleBuild> builds = ImmutableMap.builder();
	
	private final File root;
	private final String name;
	
	public ProjectBuilder(String name, File root) {
		this.name = name;
		this.root = root;
	}

	public void addManifest(AndroidManifest manifest) {
		manifests.put(manifest.getSourceSet(), manifest);
	}
	
	public void addProjectBuild(GradleBuild build) {
		builds.put(build.getProject(), build);
	}
	
	public Project build() {
		return new Project(name, root, manifests.build(), builds.build());
	}
}
