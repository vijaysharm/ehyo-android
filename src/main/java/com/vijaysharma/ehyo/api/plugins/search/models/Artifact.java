package com.vijaysharma.ehyo.api.plugins.search.models;

public class Artifact {
	private String id;
	private String g;
	private String a;
	private String latestVersion;
	private String p;
	
	public String getId() {
		return id;
	}
	
	public String getGroupId() {
		return g;
	}
	
	public String getArtifact() {
		return a;
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}
	
	public String getPackageType() {
		return p;
	}
}
