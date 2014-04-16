package com.vijaysharma.ehyo.core.models;

public class Flavor {
	private final String flavor;
	public Flavor(String flavor) {
		this.flavor = flavor;
	}
	
	public String getCompileString(BuildType buildType) {
		return "<FLAVOR????>: " + flavor;
	}
}
