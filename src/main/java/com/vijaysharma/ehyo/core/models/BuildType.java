package com.vijaysharma.ehyo.core.models;

public class BuildType {
	public static final BuildType COMPILE = new BuildType("compile");
	public static final BuildType RELEASE = new BuildType("release");
	public static final BuildType DEBUG = new BuildType("debug");
	public static final BuildType ANDROID_TEST = new BuildType("androidTest");
	
	private final String type;

	public BuildType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public String getCompileString() {
		if ( this == COMPILE ) {
			return getType();
		}

		return getType() + "Compile";
	}
}
