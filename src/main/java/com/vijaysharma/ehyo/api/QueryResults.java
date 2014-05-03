package com.vijaysharma.ehyo.api;

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
