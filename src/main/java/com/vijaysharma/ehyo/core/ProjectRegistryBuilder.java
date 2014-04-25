package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.SourceSet;
import com.vijaysharma.ehyo.core.models.SourceSetType;

/**
 * TODO: I wonder about the usefulness of this class, you can probably move a
 * lot of this logic into the {@link ProjectRegistryLoader}. But we'll keep it
 * here for now.
 */
public class ProjectRegistryBuilder {
	private final ImmutableList.Builder<String> projectList = ImmutableList.builder();
	private final Map<String, File> builds = Maps.newHashMap();
	private final ArrayListMultimap<String, File> manifests = ArrayListMultimap.create();
	private final File root;

	public ProjectRegistryBuilder(File root) {
		this.root = root;
	}

	public void addProject(String projectName) {
		this.projectList.add(projectName);
	}

	public void addProjects(List<String> projects) {
		this.projectList.addAll(projects);
	}

	public void addBuild(File build) {
		this.builds.put(build.getParentFile().getName(), build);
	}
	
	public void adddManifest(File manifest) {
		String project = manifest.getParentFile().getParentFile().getParentFile().getName();
		manifests.put(project, manifest);
	}
	
	public ProjectRegistry build() {
		if (projectList.build().isEmpty())
			projectList.add(root.getName());

		Set<String> projects = Sets.newHashSet(projectList.build());
		ImmutableMap.Builder<String, Project> projectMap = ImmutableMap.builder(); 
		// TODO: We could read in the build file, get the source sets and then
		// determine if we have a corresponding manifest file.
		for ( String projectName : projects ) {
			Map<SourceSetType, AndroidManifest> manifestMapping = Maps.newHashMap();
			List<File> projectManifestFiles = manifests.removeAll(projectName);
			for ( File file : projectManifestFiles ) {
				AndroidManifestDocument document = AndroidManifestDocument.read(file); 
				Set<String> permissions = document.getPermissions();
				SourceSetType sourceSet = new SourceSetType(file.getParentFile().getName());

				manifestMapping.put(sourceSet, new AndroidManifest(file, projectName, sourceSet, permissions));
			}
			
			File buildFile = builds.remove(projectName);
			if ( buildFile == null ) {
				// There's were no builds found for this project name. Weird.
				continue;
			}
			
			GradleBuildDocument document = GradleBuildDocument.read(buildFile);
			Set<BuildType> buildTypes = document.getBuildTypes();
			Set<Flavor> flavors = document.getFlavors();
			Set<SourceSetType> sourceSetTypes = document.getSourceSetTypes();
			List<Dependency> dependencies = document.getDependencies();
			
			ImmutableSet.Builder<SourceSet> sourceSets = ImmutableSet.builder();
			for ( SourceSetType type : sourceSetTypes ) {
				AndroidManifest manifest = manifestMapping.get(type);
				sourceSets.add(new SourceSet(type, Optional.fromNullable(manifest)));
			}
			
			GradleBuild build = new GradleBuild(projectName, buildFile, buildTypes, flavors, sourceSets.build(), dependencies);
			projectMap.put(projectName, new Project(projectName, build));
		}
		
		return new ProjectRegistry(projectMap.build());
	}
}
