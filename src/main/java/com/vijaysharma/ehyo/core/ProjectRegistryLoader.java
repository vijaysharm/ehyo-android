package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.models.GradleSettings;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ProjectRegistryLoader {
	private final ObjectFactory factory;

	public ProjectRegistryLoader() {
		this(new ObjectFactory());
	}
	
	public ProjectRegistryLoader(ObjectFactory factory) {
		this.factory = factory;
	}

	public ProjectRegistry load(File root) {
		FileObserverProjectBuilder projects = factory.create(FileObserverProjectBuilder.class);
		
		if ( root.isDirectory() )
			showFiles(root.listFiles(), projects);
		
		return projects.build();
	}

	private static void showFiles(File[] files, FileSystemObserver observer) {
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
	
	static class FileObserverProjectBuilder implements FileSystemObserver {
		private final List<GradleSettings> settings = Lists.newArrayList();
		private final List<File> manifests = Lists.newArrayList();
		private final List<File> builds = Lists.newArrayList();

		private final ObjectFactory projectFactory;
		
		public FileObserverProjectBuilder() {
			this(new ObjectFactory());
		}
		
		FileObserverProjectBuilder(ObjectFactory projectFactory) {
			this.projectFactory = projectFactory;
		}
		
		@Override
		public void onSettings(File settings) {
			this.settings.add(GradleSettings.read(settings));
		}

		@Override
		public void onManifest(File manifest) {
			this.manifests.add(manifest);
		}

		@Override
		public void onBuild(File build) {
			this.builds.add(build);
		}

		public ProjectRegistry build() {
			ProjectRegistryBuilder registry = projectFactory.create(ProjectRegistryBuilder.class);

			for ( GradleSettings setting : this.settings )
				registry.addProjects( setting.getProjects() );

			// iterate over the build files and build a view of the projects
			for ( File build : this.builds ) {
				registry.addBuild(build);
			}

			// iterate over the manifest file and keep a view on each project
			for ( File manifest : this.manifests ) {
				registry.adddManifest(manifest);
			}

			return registry.build();
		}
	}
	
	private static interface FileSystemObserver {
		void onSettings( File settings );
		void onManifest( File manifest );
		void onBuild( File build );
	}
}
