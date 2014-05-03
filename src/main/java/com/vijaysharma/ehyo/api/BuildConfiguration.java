package com.vijaysharma.ehyo.api;

import java.util.Set;


public interface BuildConfiguration {
	void addArtifact(Artifact artifact);
	void addArtifacts(Set<Artifact> artifacts);
	void removeArtifact(Artifact artifact);
	void removeArtifacts(Set<Artifact> selectedArtifacts);
	Set<Artifact> getArtifacts();
}
