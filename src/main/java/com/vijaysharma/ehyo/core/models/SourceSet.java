package com.vijaysharma.ehyo.core.models;

import java.io.File;


public class SourceSet {
	private final AndroidManifest manifests;
	private final SourceSetType sourceSet;
	private final String project;
	private final File source;
	private final File resource;
	
	public SourceSet(String project, 
					 SourceSetType sourceSet,
					 AndroidManifest manifest,
					 File source,
					 File resource) {
		this.sourceSet = sourceSet;
		this.manifests = manifest;
		this.project = project;
		this.source = source;
		this.resource = resource;
	}
	
	public String getProject() {
		return project;
	}
	
	public File getSourceDirectory() {
		return source;
	}
	
	public File getResourceDirectory() {
		return resource;
	}

	public AndroidManifest getManifest() {
		return manifests;
	}
	
	public SourceSetType getSourceSet() {
		return sourceSet;
	}
}
