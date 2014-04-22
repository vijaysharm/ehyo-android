package com.vijaysharma.ehyo.core.models;

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
}