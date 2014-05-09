package com.vijaysharma.ehyo.core;

import static com.vijaysharma.ehyo.core.utils.EStringUtil.makeFirstLetterUpperCase;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;
import com.vijaysharma.ehyo.core.models.BuildTypeDocument;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.DependencyType;
import com.vijaysharma.ehyo.core.models.FlavorDocument;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.ProjectRegistryBuilderUtil;
import com.vijaysharma.ehyo.core.models.SourceSet;
import com.vijaysharma.ehyo.core.models.SourceSetDocument;
import com.vijaysharma.ehyo.core.models.SourceSetType;

public class ProjectRegistryBuilder {
	private final ImmutableList.Builder<String> projectList = ImmutableList.builder();
	private final Map<String, File> builds = Maps.newHashMap();
	private final Set<File> manifests = Sets.newHashSet();

	public void addProject(String projectName) {
		this.projectList.add(projectName);
	}

	/**
	 * TODO: Handle Funny project names like app, app:libs, app:libs:lib 
	 */
	public void addProjects(List<String> projects) {
		this.projectList.addAll(projects);
	}

	public void addBuild(File build) {
		this.builds.put(build.getParentFile().getName(), build);
	}
	
	public void adddManifest(File manifest) {
		manifests.add(manifest);
	}
	
	public ProjectRegistry build() {
		ImmutableMap.Builder<String, Project> projectMap = ImmutableMap.builder();
		for ( Entry<String, File> buildEntry : builds.entrySet() ) {
			String projectName = buildEntry.getKey();
			File buildFile = buildEntry.getValue(); 
			File parent = buildFile.getParentFile();
			GradleBuildDocument buildDocument = GradleBuildDocument.read(buildFile);
			Set<BuildType> buildTypes = buildTypes(buildDocument.buildTypes());
			Set<Flavor> flavors = flavors(buildDocument.flavors());
			Map<SourceSetType, SourceSet> sourceSets = sourceSets(parent, buildTypes, flavors, buildDocument.sourceSets());
			Map<DependencyType, Set<Dependency>> dependencies = buildDocument.dependencies();
			
			GradleBuild build = new GradleBuild(projectName, 
												buildFile,
												buildTypes, 
												flavors, 
												sourceSets,
												dependencies);
			projectMap.put(projectName, new Project(projectName, build));
		}
		
		return new ProjectRegistry(projectMap.build());
	}

	private Map<SourceSetType, SourceSet> sourceSets(File parent, Set<BuildType> buildTypes, Set<Flavor> flavors, Multimap<String, SourceSetDocument> sourceSetDocument) {
		ImmutableMap.Builder<SourceSetType, SourceSet> sourceSet = ImmutableMap.builder();
		ProjectRegistryBuilderUtil util = new ProjectRegistryBuilderUtil(manifests, sourceSetDocument);
		util.addSourceSet("main", parent, sourceSet);
		util.addSourceSet("androidTest", parent, sourceSet);
		
		for ( BuildType type : buildTypes ) {
			util.addSourceSetWithTest(type.getType(), parent, sourceSet);
		}
		
		for ( Flavor flavor : flavors ) {
			util.addSourceSetWithTest(flavor.getFlavor(), parent, sourceSet);
		}
		
		for ( Flavor flavor : flavors ) {
			for ( BuildType type : buildTypes ) {
				String name = flavor.getFlavor() + makeFirstLetterUpperCase(type.getType());
				util.addSourceSetWithTest(name, parent, sourceSet);
			}
		}
		
		return sourceSet.build();
	}
	
	private Set<Flavor> flavors(Set<FlavorDocument> flavors) {
		ImmutableSet.Builder<Flavor> flavor = ImmutableSet.builder();
		
		for ( FlavorDocument doc : flavors ) {
			flavor.add(new Flavor(doc.getName()));
		}
		
		return flavor.build();
	}

	private Set<BuildType> buildTypes(Set<BuildTypeDocument> set) {
		ImmutableSet.Builder<BuildType> types = ImmutableSet.builder();
		types.add(new BuildType("debug"));
		types.add(new BuildType("release"));
		
		for ( BuildTypeDocument doc : set ) {
			types.add(new BuildType(doc.getName()));
		}
		
		return types.build();
	}
}
