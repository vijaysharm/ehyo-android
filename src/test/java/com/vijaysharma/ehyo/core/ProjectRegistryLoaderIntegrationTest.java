package com.vijaysharma.ehyo.core;

import static com.google.common.base.Joiner.on;
import static java.io.File.separator;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Joiner;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.DependencyType;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.SourceSet;
import com.vijaysharma.ehyo.core.models.SourceSetType;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

/**
 * TODO: Test for complicated source set e.g. androidTestFlavor1Debug 
 * TODO: Test for dependencies of type 'project' or 'files'
 */
public class ProjectRegistryLoaderIntegrationTest {
	private static final BuildType DEBUG = new BuildType("debug");
	private static final BuildType RELEASE = new BuildType("release");
	
	private static final SourceSetType MAIN = new SourceSetType("main");
	private static final SourceSetType ANDROID_TEST = new SourceSetType("androidTest");
	
	
	private static final String root = "root";
	private static final String app = "app";
	private static final String src = "src";
	private static final String main = "main";
	private static final String androidTest = "androidTest";
	private static final String res = "res";
	private static final String java = "java";
	
	private static final String settings = "settings.gradle";
	private static final String manifest = "AndroidManifest.xml";
	private static final String build = "build.gradle";
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	private File directory;
	private ProjectRegistryLoader loader;
	
	@Before
	public void before() {
		directory = folder.newFolder(root);
		loader = new ProjectRegistryLoader();
	}
	
	@Test
	public void load_simple_project() {
		writeSettings($(root, settings));
		writeParentBuild($(root, build));
		writeManifest($(root, app, src, main, manifest));
		folderInPath($(root, app, src, main, java));
		folderInPath($(root, app, src, main, res));
		write(fileInPath($(root, app, build)),
			"apply plugin: 'android'",
			"android {",
			    "compileSdkVersion 19",
			    "buildToolsVersion \"19.0.3\"",
			"}"
		);
		
		ProjectRegistry registry = loader.load(directory);
		Project project = registry.getProject(app);
		assertNotNull(project);
		
		GradleBuild build = project.getBuild();
		assertNotNull(build);
		
		// TODO: Test manifest, source and resource directory in source set
		Map<SourceSetType, SourceSet> sourceSets = build.getSourceSets();
		SourceSet actual = sourceSets.get(new SourceSetType("main"));
		assertEquals(1, sourceSets.size());
		assertEquals(MAIN, actual.getSourceSet());
		assertNotNull(actual.getManifest());
		assertNotNull(actual.getSourceDirectory());
		assertNotNull(actual.getResourceDirectory());
		
		Set<Flavor> flavors = build.getFlavors();
		assertEquals(0, flavors.size());
		
		Set<BuildType> buildTypes = build.getBuildTypes();
		assertEquals(2, buildTypes.size());
		assertEquals(true, buildTypes.contains(RELEASE));
		assertEquals(true, buildTypes.contains(DEBUG));
		
		Map<DependencyType, Set<Dependency>> dependencies = build.getDependencies();
		assertEquals(0, dependencies.size());
	}

	@Test
	public void load_simple_project_with_androidTest() {
		writeSettings($(root, settings));
		writeParentBuild($(root, build));
		
		// android Test
		folderInPath($(root, app, src, androidTest, java));
		folderInPath($(root, app, src, androidTest, res));
		
		// main
		writeManifest($(root, app, src, main, manifest));
		folderInPath($(root, app, src, main, java));
		folderInPath($(root, app, src, main, res));
		write(fileInPath($(root, app, build)),
			"apply plugin: 'android'",
			"android {",
			    "compileSdkVersion 19",
			    "buildToolsVersion \"19.0.3\"",
			"}"
		);
		
		ProjectRegistry registry = loader.load(directory);
		Project project = registry.getProject(app);
		assertNotNull(project);
		
		GradleBuild build = project.getBuild();
		assertNotNull(build);
		
		// TODO: Test manifest, source and resource directory in source set
		Map<SourceSetType, SourceSet> sourceSets = build.getSourceSets();
		assertEquals(2, sourceSets.size());
		assertEquals(MAIN, sourceSets.get(new SourceSetType("main")).getSourceSet());
		assertEquals(ANDROID_TEST, sourceSets.get(new SourceSetType("androidTest")).getSourceSet());
		
		Set<Flavor> flavors = build.getFlavors();
		assertEquals(0, flavors.size());
		
		Set<BuildType> buildTypes = build.getBuildTypes();
		assertEquals(2, buildTypes.size());
		assertEquals(true, buildTypes.contains(RELEASE));
		assertEquals(true, buildTypes.contains(DEBUG));
		
		Map<DependencyType, Set<Dependency>> dependencies = build.getDependencies();
		assertEquals(0, dependencies.size());
	}
	
	/**
	 * Project structure taken from https://gist.github.com/cyrilmottier/8234960
	 */
	@Test
	public void load_project_with_flavors_and_build_types() {
		writeSettings($(root, settings));
		writeParentBuild($(root, build));
		
		// main
		writeManifest($(root, app, src, main, manifest));
		folderInPath($(root, app, src, main, java));
		writeEmptyResourceInPath($(root, app, src, main, res, "values", "config.xml"));
		
		// avelibDebug
		folderInPath($(root, app, src, "avelibDebug", java));
		writeEmptyResourceInPath($(root, app, src, "avelibDebug", res, "values", "config.xml"));
		writeEmptyResourceInPath($(root, app, src, "avelibRelease", res, "values", "config.xml"));
		
		// avelov
		folderInPath($(root, app, src, "avelovDebug", java));
		writeEmptyResourceInPath($(root, app, src, "avelovDebug", res, "values", "config.xml"));
		writeEmptyResourceInPath($(root, app, src, "avelovRelease", res, "values", "config.xml"));
		
		write(fileInPath($(root, app, build)),
			"apply plugin: 'android'",
			"repositories {",
			    "mavenCentral()",
			"}",
			"dependencies {",
			    "compile 'com.android.support:support-v4:19.0.0'",
			    "compile 'com.google.android.gms:play-services:4.0.30'",
			    "compile 'com.squareup.retrofit:retrofit:1.2.2'",
			"}",
			"android {",
			    "compileSdkVersion 18",
			    "buildToolsVersion '19'",
			    "defaultConfig {",
			        "minSdkVersion 14",
			        "targetSdkVersion 19",
			        "versionCode 14",
			        "versionName name",
			    "}",
			    "signingConfigs {",
			        "debug {",
			            "storeFile file(\"../distribution/debug.keystore\")",
			        "}",
			        "release {",
			            "storeFile file(\"../distribution/release.keystore\")",
			            "storePassword \"XXX\"",
			            "keyAlias \"XXX\"",
			            "keyPassword \"XXX\"",
			        "}",
			    "}",
			    "productFlavors {",
			        "avelov {",
			            "packageName = \"com.cyrilmottier.android.avelov\"",
			        "}",
			        "avelib {",
			            "packageName = \"com.cyrilmottier.android.avelib\"",
			        "}",
			    "}",
			    "buildTypes {",
			        "debug {",
			            "packageNameSuffix \".debug\"",
			            "signingConfig signingConfigs.debug",
			            "versionNameSuffix \"-debug\"",
			        "}",
			        "release {",
			            "proguardFile 'config.proguard'",
			            "runProguard true",
			            "signingConfig signingConfigs.release",
			        "}",
			    "}",
			"}"
		);
		
		ProjectRegistry registry = loader.load(directory);
		Project project = registry.getProject(app);
		assertNotNull(project);
		
		GradleBuild build = project.getBuild();
		assertNotNull(build);
		
		// TODO: Test manifest, source and resource directory in source set
		Map<SourceSetType, SourceSet> sourceSets = build.getSourceSets();
		assertEquals(5, sourceSets.size());
		assertEquals(true, sourceSets.containsKey(new SourceSetType("main")));
		assertEquals(true, sourceSets.containsKey(new SourceSetType("avelibDebug")));
		assertEquals(true, sourceSets.containsKey(new SourceSetType("avelibRelease")));
		assertEquals(true, sourceSets.containsKey(new SourceSetType("avelovDebug")));
		assertEquals(true, sourceSets.containsKey(new SourceSetType("avelovRelease")));
		
		Set<Flavor> flavors = build.getFlavors();
		assertEquals(2, flavors.size());
		assertEquals(true, flavors.contains(new Flavor("avelov")));
		assertEquals(true, flavors.contains(new Flavor("avelib")));
		
		Set<BuildType> buildTypes = build.getBuildTypes();
		assertEquals(2, buildTypes.size());
		assertEquals(true, buildTypes.contains(RELEASE));
		assertEquals(true, buildTypes.contains(DEBUG));
		
	    DependencyType compile = new DependencyType("compile");
	    Map<DependencyType, Set<Dependency>> dependencies = build.getDependencies();
		Collection<Dependency> collection = dependencies.get(compile);
		assertEquals(18, dependencies.size());
		assertEquals(true, dependencies.containsKey(new DependencyType("compile")));
		assertEquals(true, dependencies.containsKey(new DependencyType("androidTestCompile")));
		assertEquals(true, dependencies.containsKey(new DependencyType("avelovCompile")));
		assertEquals(true, dependencies.containsKey(new DependencyType("avelibCompile")));
		
		assertEquals(3, collection.size());
		assertEquals(true, collection.contains(new Dependency(compile, "com.android.support:support-v4:19.0.0")));
		assertEquals(true, collection.contains(new Dependency(compile, "com.google.android.gms:play-services:4.0.30")));
		assertEquals(true, collection.contains(new Dependency(compile, "com.squareup.retrofit:retrofit:1.2.2")));
		
		Set<Artifact> artifacts = build.getArtifacts(compile);
		assertEquals(3, artifacts.size());
		assertEquals(true, artifacts.contains(Artifact.read("com.android.support:support-v4:19.0.0")));
		assertEquals(true, artifacts.contains(Artifact.read("com.google.android.gms:play-services:4.0.30")));
		assertEquals(true, artifacts.contains(Artifact.read("com.squareup.retrofit:retrofit:1.2.2")));		
	}
	
	private static String $(String...path) {
		return on(separator).join(path);
	}
	
	private void writeEmptyResourceInPath(String path) {
		File file = fileInPath(path);
		write(file,
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>",
			"<resources>",
			"</resources>"
		);
	}
	
	private void writeManifest(String path) {
		File appManifest = fileInPath(path);
		write(appManifest,
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
			"<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"com.vijay.app\">",
			  "<application>",
			    "<activity android:name=\"com.vijay.app.MainActivity\">",
			      "<intent-filter>",
			        "<action android:name=\"android.intent.action.MAIN\" />",
			        "<category android:name=\"android.intent.category.LAUNCHER\" />",
			      "</intent-filter>",
			    "</activity>",
			  "</application>",
			"</manifest>"
		);
	}

	private void writeParentBuild(String path) {
		File parentBuild = fileInPath(path);
		write(parentBuild,
			"buildscript {",
			    "repositories {",
			        "mavenCentral()",
			    "}",
			    "dependencies {",
			        "classpath 'com.android.tools.build:gradle:0.9.+'",
			    "}",
			"}",
			"allprojects {",
			    "repositories {",
			        "mavenCentral()",
			    "}",
			"}"
		);
	}

	private void writeSettings(String path) {
		File settings = fileInPath(path);
		write(settings, "include ':app'");
	}
	
	private void write(File file, String... contents) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			for ( String line : contents )
				out.write(line + "\n");
	        
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	private void folderInPath(String path) {
		String[] split = path.split(separator);
		for ( int i = 1; i <= split.length; i++ ) {
			String s = Joiner.on(separator).join(Arrays.copyOf(split, i));
			folder.newFolder(s);
		}
	}
	
	private File fileInPath(String path) {
		try {
			String[] split = path.split(separator);
			File file = null;
			for ( int i = 1; i <= split.length; i++ ) {
				String s = Joiner.on(separator).join(Arrays.copyOf(split, i));
				if ( i == split.length ) {
					file = folder.newFile(s);
				} else {
					folder.newFolder(s);
				}
			}
			
			return file;
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
}
