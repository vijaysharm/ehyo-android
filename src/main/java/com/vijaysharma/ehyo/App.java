package com.vijaysharma.ehyo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.annotations.MakeLogger;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleSettings;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class App 
{
	@MakeLogger
	private static Logger l;
	
    public static void main( String[] args ) throws Exception
    {
    	initializeLoggers();
    	
    	String directory = "/Users/vsharma/programming/android/MyApplication";
    	File file = new File( directory );
    	if ( ! file.isDirectory() ) {
    		l.error( "Expected a directory. Got {}", directory );
    		return;
    	}
    	
    	String root = file.getName();
    	
    	FileObserverProjectBuilder observer = new FileObserverProjectBuilder( root );
		showFiles(file.listFiles(), observer);
		
		ProjectRegistry registry = observer.build();
    }
    
    public static void initializeLoggers() throws Exception {
    	Reflections reflections = new Reflections("com.vijaysharma.ehyo", new FieldAnnotationsScanner());
		Set<Field> set = reflections.getFieldsAnnotatedWith(MakeLogger.class);
    	for (Field field : set) {
    		field.set(null, LoggerFactory.getLogger(field.getDeclaringClass()));
		}	
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
    
    private static class FileObserverProjectBuilder implements FileSystemObserver {
		private final String root;
		private final List<GradleSettings> settings = Lists.newArrayList();
		private final List<AndroidManifest> manifests = Lists.newArrayList();
		private final List<GradleBuild> builds = Lists.newArrayList();
		
		public FileObserverProjectBuilder(String root) {
			this.root = root;
		}

		@Override
		public void onSettings(File settings) {
			this.settings.add(GradleSettings.read(settings));
		}

		@Override
		public void onManifest(File manifest) {
			this.manifests.add(AndroidManifest.read(manifest));
		}

		@Override
		public void onBuild(File build) {
			this.builds.add(GradleBuild.read(build));
		}
		
		public ProjectRegistry build() {
			ProjectRegistryBuilder registry = new ProjectRegistryBuilder();
			registry.addProject(root);
			
			if ( ! settings.isEmpty() ) {
				// register a bunch of projects
				for ( GradleSettings setting : this.settings )
					registry.addProjects( setting.getProjects() );
			} 
			
			// iterate over the build files and build a view of the projects
			for ( GradleBuild build : this.builds ) {
				registry.addBuild(build);
			}

			// iterate over the manifest file and keep a view on each project
			for ( AndroidManifest manifest : this.manifests ) {
				registry.adddManifest(manifest);
			}
			
			return registry.build();
		}
    }
}
