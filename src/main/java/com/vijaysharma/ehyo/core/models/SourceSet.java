package com.vijaysharma.ehyo.core.models;

import java.util.Set;

import com.google.common.base.Optional;


public class SourceSet {
	private final Optional<AndroidManifest> manifests;
	private final SourceSetType sourceSet;
	
	public SourceSet(SourceSetType sourceSet, Optional<AndroidManifest> optional) {
		this.sourceSet = sourceSet;
		this.manifests = optional;
	}
	
	public Set<AndroidManifest> getManifests() {
		return manifests.asSet();
	}
	
	public SourceSetType getSourceSet() {
		return sourceSet;
	}
}
