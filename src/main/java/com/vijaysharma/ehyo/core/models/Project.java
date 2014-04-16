package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Should well describe a project in android
 * \project\src\[main, debug, release, flavor]\[java, res] 
 * A single project break down items to variants, i.e 1 project has N variants
 */
public class Project {
	private final File applicationRoot;
	private final String name;
	private final Map<String, GradleBuild> builds;
	private final Map<String, AndroidManifest> manifests;

	public Project(String name, File root, Map<String, AndroidManifest> manifests, Map<String, GradleBuild> builds) {
		this.name = name;
		this.applicationRoot = root;
		this.manifests = manifests;
		this.builds = builds;
	}
	
	public Collection<AndroidManifest> getManifests() {
		return manifests.values();
	}
	
	public Collection<GradleBuild> getBuilds() {
		return builds.values();
	}
	
	public String getName() {
		return name;
	}
}
