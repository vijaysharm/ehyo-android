package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.utils.EFileUtil.readLines;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GradleBuildDocument implements AsListOfStrings {
	public static GradleBuildDocument read(File file) {
		return new GradleBuildDocument(readLines(file), file);
	}
	
	private final List<String> lines;
	private final GradleBuildDocumentModel model;
	private final File file;
	
	private GradleBuildDocument(List<String> lines, File file) {
		this.lines = lines;
		this.model = new GradleBuildDocumentModel(lines);
		this.file = file;
	}
	
	@Override
	public List<String> toListOfStrings() {
		return lines;
	}

	public Set<BuildType> getBuildTypes() {
		ImmutableSet.Builder<BuildType> builtypes = ImmutableSet.builder();
		builtypes.add(BuildType.ANDROID_TEST);
		builtypes.add(BuildType.COMPILE);
		builtypes.add(BuildType.DEBUG);
		builtypes.add(BuildType.RELEASE);
		
		String key = "root.android.buildTypes.";
		Set<String> keys = model.getKeysStartingWith(key);
		for ( String k : keys ) {
			String build = k.substring(key.length()+1);
			builtypes.add(new BuildType(build));
		}
		
		return builtypes.build();
	}
	
	public Set<Flavor> getFlavors() {
		ImmutableSet.Builder<Flavor> flavors = ImmutableSet.builder();
		String key = "root.android.productFlavors.";
		Set<String> keys = model.getKeysStartingWith(key);
		
		for ( String k : keys ) {
			String flavor = k.substring(key.length()+1);
			flavors.add(new Flavor(flavor));
		}
		
		return flavors.build();
	}
	
	public Set<String> getDependencies(BuildType buildType, Flavor flavor) {
		ImmutableSet.Builder<String> dependencies = ImmutableSet.builder();
		Collection<String> properties = model.getProperties("root.dependencies");

		for ( String property : properties ) {
			if (property.startsWith(buildType.getCompileString(flavor))) {
				String library = parseLibrary(buildType, null, property);
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
					String library = parseLibrary(buildType, null, property);
					dependencies.add(new Dependency(buildType, null, library));
				}
				
				for ( Flavor flavor : flavors ) {
					if (property.startsWith(buildType.getCompileString(flavor))) {
						String library = parseLibrary(buildType, flavor, property);
						dependencies.add(new Dependency(buildType, flavor, library));
					}	
				}
			}
		}
		
		return dependencies.build();
	}

	private String parseLibrary(BuildType buildType, Flavor flavor, String property) {
		int indexof = buildType.getCompileString(flavor).length();
		return property.substring(indexof + 2, property.length() -1);
	}
	
	/**
	 * TODO: This may add to the dependencies defined in buildscript 
	 */
//	public GradleBuildDocument appendDependency(String dependency) {
//		for ( int index = 0; index < lines.size(); index++ ) {
//			String line = lines.get(index);
//			if ( line.trim().startsWith("dependencies") ) {
//				lines.add(index + 1, "\t" + dependency);
//				break;
//			}
//		}
//		
//		return this;
//	}

	public void addDependency(Dependency dependency) {
		
	}
	
	public void removeDependency(Dependency dependency) {
		
	}

	public GradleBuildDocument copy() {
		return GradleBuildDocument.read(file);
	}
}
