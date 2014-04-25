package com.vijaysharma.ehyo.core.models;

import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;

public class Dependency {
	private final BuildType buildType;
	private final Flavor flavor;
	private final String dependency;
	
	public Dependency(BuildType buildType, Flavor flavor, String dependency) {
		this.buildType = buildType;
		this.flavor = flavor;
		this.dependency = dependency;
	}

	public BuildType getBuildType() {
		return buildType;
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public String getDependency() {
		return dependency;
	}

	@Override
	public String toString() {
		return "Dependency [buildType=" + buildType + ", flavor=" + flavor
				+ ", dependency=" + dependency + "]";
	}
}
