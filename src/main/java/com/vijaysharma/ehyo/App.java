package com.vijaysharma.ehyo;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleSettings;

public class App 
{
	private static final Logger l = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws Exception
    {
    	String directory = "/Users/vsharma/programming/android/MyApplication";
    	File file = new File( directory );
    	if ( ! file.isDirectory() ) {
    		l.info( "Expected a directory. Got: " + directory );
    		return;
    	}
    	
    	String root = file.getName();
    	l.info("Root: " + root);
    	
    	final ProjectRegistryBuilder registry = new ProjectRegistryBuilder( root );
    	showFiles(file.listFiles(), new FileSystemObserver() {
			@Override
			public void onSettings( File settings ) {
				registry.addSettings( settings );
			}

			@Override
			public void onManifest( File manifest ) {
				registry.addManifest( manifest );
			}

			@Override
			public void onBuild( File build ) {
				registry.addBuild( build );
			}
		});
    }
    
	public static void showFiles(File[] files, FileSystemObserver observer) {
		if (files == null || files.length == 0)
			return;

		for (File file : files) {
			if (file.isDirectory()) {
				if (! "build".equals(file.getName()))
					showFiles(file.listFiles(), observer);
			} else {
				if ("settings.gradle".equals(file.getName()))
					observer.onSettings(file);
				else if ("AndroidManifest.xml".equals(file.getName()))
					observer.onManifest(file);
				else if ("build.gradle".equals(file.getName()))
					observer.onBuild(file);
			}
		}
	}
    
    private interface FileSystemObserver {
    	void onSettings( File settings );
    	void onManifest( File manifest );
    	void onBuild( File build );
    }
    
    private static class ProjectRegistryBuilder {
		private final String root;
		private final List<GradleSettings> settings = Lists.newArrayList();
		private final List<AndroidManifest> manifests = Lists.newArrayList();
		private final List<GradleBuild> builds = Lists.newArrayList();
		
		public ProjectRegistryBuilder(String root) {
			this.root = root;
		}

		public void addSettings(File settings) {
			this.settings.add(GradleSettings.read(settings));
		}

		public void addManifest(File manifest) {
			this.manifests.add(AndroidManifest.read(manifest));
		}

		public void addBuild(File build) {
			this.builds.add(GradleBuild.read(build));
		}
		
		public void build() {
			if ( settings.isEmpty() ) {
				// create a 'root' project
			} else {
				// register a bunch of projects
			}
			
			// iterate over the build files and build a view of the projects
			// iterate over the manifest file and keep a view on each project
		}
    }
}
