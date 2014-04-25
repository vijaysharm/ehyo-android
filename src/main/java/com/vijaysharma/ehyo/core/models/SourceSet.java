package com.vijaysharma.ehyo.core.models;


public class SourceSet {
	private final AndroidManifest manifests;
	private final SourceSetType sourceSet;
	private final String project;
	
	public SourceSet(String project, SourceSetType sourceSet, AndroidManifest manifest) {
		this.sourceSet = sourceSet;
		this.manifests = manifest;
		this.project = project;
	}
	
	public String getProject() {
		return project;
	}
	
	public AndroidManifest getManifests() {
		return manifests;
	}
	
	public SourceSetType getSourceSet() {
		return sourceSet;
	}
}
