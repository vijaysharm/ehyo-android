package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ProjectRegistryBuilder {
	private static final Logger l = LoggerFactory.getLogger(ProjectRegistryBuilder.class);
	
	private final ImmutableList.Builder<String> projectList = ImmutableList.builder();
	private final Map<String, GradleBuild> builds = Maps.newHashMap();
	private final Map<String, AndroidManifest> manifests = Maps.newHashMap();
	private final File root;

	
	public ProjectRegistryBuilder(File root) {
		this.root = root;
	}

	public void addProject(String root) {
		this.projectList.add( root );
	}

	public void addProjects(List<String> projects) {
		this.projectList.addAll(projects);
	}

	public void addBuild(GradleBuild build) {
		this.builds.put(build.getProject(), build);
	}
	
	public void adddManifest(AndroidManifest manifest) {
		this.manifests.put(manifest.getProject(), manifest);
	}
	
	public ProjectRegistry build() {
		Set<String> projects = Sets.newHashSet(projectList.build());
		ImmutableMap.Builder<String, Project> projectMap = ImmutableMap.builder();
		
		l.info("Found {} projects [{}]", projects.size(), Joiner.on(", ").join(projects));
		for ( String projectName : projects ) {
			ProjectBuilder project = new ProjectBuilder();
			
			GradleBuild build = builds.remove(projectName);
			addBuildToProject(project, build);
			
			AndroidManifest manifest = manifests.remove(projectName);
			addManifestToProject(project, manifest);
			
			projectMap.put(projectName, project.build());
		}
		
		if ( manifests.size() != 0 ) {
			l.warn("There following manifests [{}] were not associated with a project", 
					Joiner.on(", ").join(manifests.keySet()));
		}

		if ( builds.size() != 0 ) {
			l.warn("There following builds [{}] were not associated with a project", 
					Joiner.on(", ").join(manifests.keySet()));
		}
		
		return new ProjectRegistry(root, projectMap.build());
	}

	private void addManifestToProject(ProjectBuilder project, AndroidManifest manifest) {
		
	}

	private void addBuildToProject(ProjectBuilder project, GradleBuild build) {
		
	}
}
