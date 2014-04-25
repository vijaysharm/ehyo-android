package com.vijaysharma.ehyo.core.models;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;

public class ProjectRegistry {
	private final Map<String, Project> projects;

	public ProjectRegistry(Map<String, Project> projects) {
		this.projects = projects;
	}
	
	public Project geProject(GradleBuild build) {
		return projects.get(build.getProject());
	}
	
	public List<GradleBuild> getAllGradleBuilds() {
		return getAllGradleBuilds(Functions.<GradleBuild>identity());
	}
	
	public <T> List<T> getAllGradleBuilds(Function<GradleBuild, T> transform) {
		ImmutableList.Builder<T> builds = ImmutableList.builder();
		for ( Project project : projects.values() ) {
			builds.add(transform.apply(project.getBuild()));
		}
		
		return builds.build();
	}
}
