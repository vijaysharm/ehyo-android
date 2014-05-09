package com.vijaysharma.ehyo.api;

import static junit.framework.Assert.assertEquals;

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
	
	@Test
	public void read_returns_null_for_file_type_artifacts() {
		assertEquals(null, Artifact.read("files('libs/foo.jar')"));
	}
	
	@Test
	public void read_returns_null_for_project_type_artifacts() {
		assertEquals(null, Artifact.read("project(':libraries:lib1')"));
	}
}
