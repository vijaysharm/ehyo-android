package com.vijaysharma.ehyo.core.models;

import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;

public class Dependency {
	private final BuildType buildType;
	private final Flavor flavor;
	private final Artifact artifact;
	
	public Dependency(BuildType buildType, Flavor flavor, Artifact dependency) {
		this.buildType = buildType;
		this.flavor = flavor;
		this.artifact = dependency;
	}

	public BuildType getBuildType() {
		return buildType;
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	@Override
	public String toString() {
		return "Dependency [buildType=" + buildType + ", flavor=" + flavor
				+ ", dependency=" + artifact + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buildType == null) ? 0 : buildType.hashCode());
		result = prime * result
				+ ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result + ((flavor == null) ? 0 : flavor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dependency other = (Dependency) obj;
		if (buildType == null) {
			if (other.buildType != null)
				return false;
		} else if (!buildType.equals(other.buildType))
			return false;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (flavor == null) {
			if (other.flavor != null)
				return false;
		} else if (!flavor.equals(other.flavor))
			return false;
		return true;
	}
}
