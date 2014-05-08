package com.vijaysharma.ehyo.api;

import org.junit.Test;

public class ArtifactTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void read_throws_on_empty_library() {
		Artifact.read("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void read_throws_on_null_library() {
		Artifact.read(null);
	}
}
