package com.vijaysharma.ehyo.core.models;

import static junit.framework.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class GradleBuildDocumentTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void can_read_all_flavors() {
    	List<String> lines = Lists.newArrayList(
			"android {",
				"productFlavors {",
			        "flavor1 {",
			            "packageName \"com.example.flavor1\"",
			            "versionCode 20",
			        "}",
			        "flavor2 {",
			            "packageName \"com.example.flavor2\"",
			            "minSdkVersion 14",
			        "}",
			    "}",
			"}"
    	);
    	
    	GradleBuildDocument document = new GradleBuildDocument(lines);
    	Set<FlavorDocument> flavors = document.flavors();
    	Collection<String> flavorNames = Collections2.transform(flavors, new Function<FlavorDocument, String>() {
			@Override
			public String apply(FlavorDocument input) {
				return input.getName();
			}
    	});
    	
    	assertEquals(2, flavors.size());
    	assertEquals(2, flavorNames.size());
    	assertEquals(true, flavorNames.contains("flavor1"));
    	assertEquals(true, flavorNames.contains("flavor2"));
    }
    
    @Test
    public void can_read_all_build_types() {
    	List<String> lines = Lists.newArrayList(
			"android {",
				"buildTypes {",
			        "debug {",
			            "packageNameSuffix \".debug\"",
			        "}",
			        "jnidebug.initWith(buildTypes.debug)",
			        "jnidebug {",
			            "packageNameSuffix \".jnidebug\"",
			            "jnidebugBuild true",
			        "}",
			    "}",
			"}"
    	);

    	GradleBuildDocument document = new GradleBuildDocument(lines);
    	Set<BuildTypeDocument> buildTypes = document.buildTypes();
    	assertEquals(2, buildTypes.size());
    	
    	Collection<String> buildTypeNames = Collections2.transform(buildTypes, new Function<BuildTypeDocument, String>() {
			@Override
			public String apply(BuildTypeDocument input) {
				return input.getName();
			}
    	});
    	assertEquals(2, buildTypeNames.size());
    	assertEquals(true, buildTypeNames.contains("debug"));
    	assertEquals(true, buildTypeNames.contains("jnidebug"));
    }
    
    @Test
    public void can_read_source_sets() {
    	List<String> lines = Lists.newArrayList(
			"android {",
				"sourceSets.jnidebug.setRoot('foo/jnidebug')",
				"sourceSets {",
			        "main {",
			            "manifest.srcFile 'AndroidManifest.xml'",
			            "java.srcDirs = ['src']",
			            "resources.srcDirs = ['src']",
			            "aidl.srcDirs = ['src']",
			            "renderscript.srcDirs = ['src']",
			            "res.srcDirs = ['res']",
			            "assets.srcDirs = ['assets']",
			        "}",

			        "androidTest.setRoot('tests')",
			        "debug.java.srcDirs = ['src/java']",
			        "release {",
			            "java {",
			                "srcDir 'src/java'",
			            "}",
			            "resources {",
			                "srcDir 'src/resources'",
			            "}",
			        "}",
			    "}",
			"}"
    	);
    	
    	GradleBuildDocument document = new GradleBuildDocument(lines);
    	Multimap<String, SourceSetDocument> sourceSets = document.sourceSets();
    	Set<String> sourceSetNames = sourceSets.keySet();
    	
    	assertEquals(6, sourceSets.size());
    	assertEquals(5, sourceSetNames.size());
    	assertEquals(true, sourceSetNames.contains("debug"));
    	assertEquals(true, sourceSetNames.contains("jnidebug"));
    	assertEquals(true, sourceSetNames.contains("main"));
    	assertEquals(true, sourceSetNames.contains("androidTest"));
    	assertEquals(true, sourceSetNames.contains("release"));
    }
}
