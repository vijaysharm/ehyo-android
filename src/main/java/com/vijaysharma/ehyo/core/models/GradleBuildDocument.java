package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.utils.EFileUtil.readLines;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;

public class GradleBuildDocument implements AsListOfStrings {
	public static GradleBuildDocument read(File file) {
		return new GradleBuildDocument(readLines(file));
	}
	
	private final GradleBuildDocumentModel model;
	
	private GradleBuildDocument(List<String> lines) {
		this.model = new GradleBuildDocumentModel(lines);
	}
	
	@Override
	public List<String> toListOfStrings() {
		return model.getLines();
	}

	/**
	 * TODO: I should read the types from the build file. If the user wants to
	 * modify a non-existent one, we should let the user know, and create the
	 * appropriate section for it
	 */
	public Set<SourceSetType> getSourceSetTypes() {
		ImmutableSet.Builder<SourceSetType> sourceSets = ImmutableSet.builder();
		sourceSets.add(SourceSetType.MAIN);
		sourceSets.add(SourceSetType.DEBUG);
		sourceSets.add(SourceSetType.RELEASE);
		sourceSets.add(SourceSetType.ANDROID_TEST);
		
		String key = "root.android.sourceSets.";
		Set<String> keys = model.getKeysStartingWith(key);
		for ( String k : keys ) {
			String source = k.substring(key.length());
			sourceSets.add(new SourceSetType(source));
		}
		
		return sourceSets.build();
	}
	
	/**
	 * TODO: I should read the types from the build file. If the user wants to
	 * modify a non-existent one, we should let the user know, and create the
	 * appropriate section for it
	 */
	public Set<BuildType> getBuildTypes() {
		ImmutableSet.Builder<BuildType> builtypes = ImmutableSet.builder();
		builtypes.add(BuildType.ANDROID_TEST);
		builtypes.add(BuildType.COMPILE);
		builtypes.add(BuildType.DEBUG);
		builtypes.add(BuildType.RELEASE);
		
		String key = "root.android.buildTypes.";
		Set<String> keys = model.getKeysStartingWith(key);
		for ( String k : keys ) {
			String build = k.substring(key.length());
			builtypes.add(new BuildType(build));
		}
		
		return builtypes.build();
	}
	
	public Set<Flavor> getFlavors() {
		ImmutableSet.Builder<Flavor> flavors = ImmutableSet.builder();
		String key = "root.android.productFlavors.";
		Set<String> keys = model.getKeysStartingWith(key);
		
		for ( String k : keys ) {
			String flavor = k.substring(key.length());
			flavors.add(new Flavor(flavor));
		}
		
		return flavors.build();
	}
	
	public Set<Artifact> getDependencies(BuildType buildType, Flavor flavor) {
		ImmutableSet.Builder<Artifact> dependencies = ImmutableSet.builder();
		Collection<String> properties = model.getProperties("root.dependencies");

		for ( String property : properties ) {
			if (property.startsWith(buildType.getCompileString(flavor))) {
				Artifact library = parseLibrary(buildType, null, property);
				dependencies.add(library);
			}
		}
		
		return dependencies.build();
	}
	
	public List<Dependency> getDependencies() {
		ImmutableList.Builder<Dependency> dependencies = ImmutableList.builder();
		Collection<String> properties = model.getProperties("root.dependencies");
	
		Set<Flavor> flavors = getFlavors();
		Set<BuildType> buildTypes = getBuildTypes();
		
		for ( String property : properties ) {
			for ( BuildType buildType : buildTypes ) {
				if (property.startsWith(buildType.getCompileString())) {
					Artifact library = parseLibrary(buildType, null, property);
					dependencies.add(new Dependency(buildType, null, library));
				}
				
				for ( Flavor flavor : flavors ) {
					if (property.startsWith(buildType.getCompileString(flavor))) {
						Artifact library = parseLibrary(buildType, flavor, property);
						dependencies.add(new Dependency(buildType, flavor, library));
					}	
				}
			}
		}
		
		return dependencies.build();
	}

	private Artifact parseLibrary(BuildType buildType, Flavor flavor, String property) {
		int indexof = buildType.getCompileString(flavor).length();
		String library = property.substring(indexof + 2, property.length() -1);
		
		return Artifact.read(library);
	}

	public void addDependencies(Set<Dependency> toBeAdded) {
		for ( Dependency dependency : toBeAdded ) {
			model.addTo("root.dependencies", formatDependency(dependency));
		}
	}
	
	public void removeDependencies(Set<Dependency> toBeRemoved) {
		for ( Dependency dependency : toBeRemoved ) {
			model.removeFrom("root.dependencies", formatDependency(dependency));
		}
	}

	private String formatDependency(Dependency lib) {
		StringBuilder dependency = new StringBuilder();

		BuildType buildType = lib.getBuildType();
		Flavor flavor = lib.getFlavor();

		String compileString = buildType.getCompileString(flavor);
		dependency
			.append("    ")
			.append(compileString)
			.append(" \'" + lib.getArtifact() + "\'");

		return dependency.toString();
	}
}
