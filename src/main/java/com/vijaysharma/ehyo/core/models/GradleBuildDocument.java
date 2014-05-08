package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.utils.EFileUtil.readLines;
import static com.vijaysharma.ehyo.core.utils.EStringUtil.makeFirstLetterUpperCase;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import joptsimple.internal.Strings;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class GradleBuildDocument implements AsListOfStrings {
	public static GradleBuildDocument read(File file) {
		return new GradleBuildDocument(readLines(file));
	}
	
	private final GradleBuildDocumentModel model;
	
	GradleBuildDocument(List<String> lines) {
		this.model = new GradleBuildDocumentModel(lines);
	}
	
	@Override
	public List<String> toListOfStrings() {
		return model.getLines();
	}

	public Set<FlavorDocument> flavors() {
		ImmutableSet.Builder<FlavorDocument> flavors = ImmutableSet.builder();
		String key = "root.android.productFlavors.";
		Set<String> keys = model.getKeysStartingWith(key);
		
		for ( String k : keys ) {
			String flavorName = k.substring(key.length());
			Collection<String> properties = model.getProperties(k);
			flavors.add(FlavorDocument.read(flavorName, properties));
		}
		
//		Collection<String> properties = model.getProperties("root.android.defaultConfig");
//		flavors.add(FlavorDocument.read("defaultConfig", properties));
		
		return flavors.build();		
	}
	
	public Set<BuildTypeDocument> buildTypes() {
		ImmutableSet.Builder<BuildTypeDocument> buildTypes = ImmutableSet.builder();
		String key = "root.android.buildTypes.";
		Set<String> keys = model.getKeysStartingWith(key);
		
		for ( String k : keys ) {
			String buildName = k.substring(key.length());
			Collection<String> properties = model.getProperties(k);
			buildTypes.add(BuildTypeDocument.read(buildName, properties));
		}
		
		return buildTypes.build();
	}
	
	public Multimap<String, SourceSetDocument> sourceSets() {
		ImmutableMultimap.Builder<String, SourceSetDocument> sourceSets = ImmutableMultimap.builder();
		String key = "root.android.sourceSets";
		Set<String> keys = model.getKeysStartingWith(key);
		
		for ( String k : keys ) {
			String buildName = k.substring(key.length());
			if ( Strings.isNullOrEmpty(buildName) ) {
				Collection<String> properties = model.getProperties(k);
				for ( String property : properties ) {
					int indexOf = property.indexOf(".");
					String name = property.substring(0, indexOf);
					String prop = property.substring(indexOf + 1, property.length());
					sourceSets.put(name, SourceSetDocument.read(name, Lists.newArrayList(prop)));
				}
			} else if (buildName.startsWith(".")) {
				String cleaned = buildName.substring(1);
				int indexOf = cleaned.indexOf(".");
				if ( indexOf == -1 ) {
					Collection<String> properties = model.getProperties(k);
					sourceSets.put(cleaned, SourceSetDocument.read(cleaned, properties));
				} else {
					String name = cleaned.substring(0, indexOf);
					Collection<String> properties = model.getProperties(k);
					sourceSets.put(name, SourceSetDocument.read(name, properties));
				}
			}
		}
		
		Collection<String> properties = model.getProperties("root.android");
		for ( String property : properties ) {
			if ( ! property.startsWith("sourceSets") )
				continue;
			
			String p = property.substring("sourceSets.".length());
			
			int indexOf = p.indexOf(".");
			String name = p.substring(0, indexOf);
			String prop = p.substring(indexOf + 1, p.length());
			sourceSets.put(name, SourceSetDocument.read(name, Lists.newArrayList(prop)));
		}
		
		return sourceSets.build();
	}
	
	public Multimap<DependencyType, Dependency> dependencies() {
		Set<BuildTypeDocument> buildTypes = buildTypes();
		Set<FlavorDocument> flavors = flavors();
		Set<String> dependencies = readDependencies();
		
		ImmutableMultimap.Builder<DependencyType, Dependency> d = ImmutableMultimap.builder();
		for ( String deps : dependencies ) {
			addDependency(deps, "compile", d);
			addDependency(deps, "androidTestCompile", d);
			
			for ( BuildTypeDocument type : buildTypes ) {
				String name = type.getName() + "Compile";
				String test = "androidTest" + makeFirstLetterUpperCase(name);
				
				addDependency(deps, name, d);
				addDependency(deps, test, d);
			}
			
			for ( FlavorDocument flavor : flavors ) {
				String name = flavor.getName() + "Compile";
				String test = "androidTest" + makeFirstLetterUpperCase(name);
				
				addDependency(deps, name, d);
				addDependency(deps, test, d);
			}
			
			for ( FlavorDocument flavor : flavors ) {
				for ( BuildTypeDocument type : buildTypes ) {
					String name = flavor.getName() + makeFirstLetterUpperCase(type.getName()) + "Compile";
					String test = "androidTest" + makeFirstLetterUpperCase(name);
					
					addDependency(deps, name, d);
					addDependency(deps, test, d);
				}
			}
		}
		
		return d.build();
	}

	private void addDependency(String dependency, String id, ImmutableMultimap.Builder<DependencyType, Dependency> dependencies) {
		String clean = dependency.trim();
		if (clean.startsWith(id + " ")) {
			DependencyType type = new DependencyType(id);
			String library = clean.substring(clean.indexOf(" ") + 1, clean.length()).trim();
			library = library.replace("'", "");
			
			dependencies.put(type, new Dependency(type, library));
		}
	}

	private Set<String> readDependencies() {
		ImmutableSet.Builder<String> dependencies = ImmutableSet.builder();
		Collection<String> properties = model.getProperties("root.dependencies");

		for ( String property : properties ) {
			dependencies.add(property);
		}
		
		return dependencies.build();
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
	
	private String formatDependency(Dependency dependency) {
		return dependency.getType() + " '" + dependency.getLibrary() + "'";
	}
}
