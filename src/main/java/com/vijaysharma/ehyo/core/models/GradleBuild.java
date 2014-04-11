package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

public class GradleBuild implements HasDocument {
	public static GradleBuild read(File file) {
		return new GradleBuild(file);
	}
	
	private final File file;
	private final String id;
	
	public GradleBuild(File file) {
		this.file = file;
		id = file.getAbsolutePath();
	}

	public String getProject() {
		return file.getParentFile().getName();
	}

	public File getFile() {
		return this.file;
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * TODO: Should read the file and determine all build types (including ones
	 * that are created with initWith)
	 */
	public List<BuildType> getBuildTypes() {
		List<BuildType> buildTypes = Lists.newArrayList();
		buildTypes.add(BuildType.COMPILE);
		buildTypes.add(BuildType.DEBUG);
		buildTypes.add(BuildType.RELEASE);
		buildTypes.add(BuildType.ANDROID_TEST);
		
		return buildTypes;
	}

	public List<Flavor> getFlavors() {
		return Lists.<Flavor>newArrayList();
	}
	
	@Override
	public GradleBuildDocument asDocument() {
		return GradleBuildDocument.read(this.file, this.id);
	}
}
