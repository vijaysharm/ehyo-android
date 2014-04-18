package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;
import java.util.Set;

public class GradleBuild implements HasDocument {
	public static GradleBuild read(File file) {
		return new GradleBuild(file);
	}
	
	private final File file;
	private final String id;
	private final GradleBuildDocument document;
	
	public GradleBuild(File file) {
		this.file = file;
		this.id = file.getAbsolutePath();
		this.document = GradleBuildDocument.read(this.file);
	}

	public String getProject() {
		return file.getParentFile().getName();
	}

	@Override
	public File getFile() {
		return this.file;
	}
	
	public String getId() {
		return id;
	}
	
	public List<Dependency> getDependencies() {
		return document.getDependencies();
	}
	
	/**
	 * TODO: Should read the file and determine all build types (including ones
	 * that are created with initWith)
	 */
	public Set<BuildType> getBuildTypes() {
		return document.getBuildTypes();
	}

	/**
	 * TODO: Should read the file and determine all build types (including ones
	 * that are created with initWith)
	 */
	public Set<Flavor> getFlavors() {
		return document.getFlavors();
	}
	
	@Override
	public GradleBuildDocument asDocument() {
		return this.document.copy();
	}

	public Set<String> getDependencies(BuildType buildType, Flavor flavor) {
		return document.getDependencies(buildType, flavor);
	}
}
