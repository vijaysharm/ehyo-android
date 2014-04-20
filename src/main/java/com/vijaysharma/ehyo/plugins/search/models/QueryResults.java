package com.vijaysharma.ehyo.plugins.search.models;

public class QueryResults {
	private int numFound;
	private Artifact[] docs;
	
	public int size() {
		return numFound;
	}
	
	public Artifact[] getArtifacts() {
		return docs;
	}
}
