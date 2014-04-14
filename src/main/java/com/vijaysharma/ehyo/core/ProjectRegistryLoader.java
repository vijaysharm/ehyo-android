package com.vijaysharma.ehyo.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleSettings;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ProjectRegistryLoader {
	private final FileConstructorFactory<FileObserverProjectBuilder> factory;

	public ProjectRegistryLoader() {
		this(new FileConstructorFactory<FileObserverProjectBuilder>());
	}
	
	public ProjectRegistryLoader(FileConstructorFactory<FileObserverProjectBuilder> factory) {
		this.factory = factory;
	}

	public ProjectRegistry load(File root) {
		FileObserverProjectBuilder projects = factory.create(FileObserverProjectBuilder.class, root);
		
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
	
	static class FileConstructorFactory<T> {
		T create(Class<T> clazz, File root) {
			try {
				Constructor<T> constructor = clazz.getConstructor(File.class);
				return constructor.newInstance(root);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class FileObserverProjectBuilder implements FileSystemObserver {
		private final File root;
		private final List<GradleSettings> settings = Lists.newArrayList();
		private final List<AndroidManifest> manifests = Lists.newArrayList();
		private final List<GradleBuild> builds = Lists.newArrayList();

		private final FileConstructorFactory<ProjectRegistryBuilder> projectFactory;
		
		public FileObserverProjectBuilder(File root) {
			this(root, new FileConstructorFactory<ProjectRegistryBuilder>());
		}
		
		FileObserverProjectBuilder(File root, FileConstructorFactory<ProjectRegistryBuilder> projectFactory) {
			this.root = root;
			this.projectFactory = projectFactory;
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
			ProjectRegistryBuilder registry = projectFactory.create(ProjectRegistryBuilder.class, root);
			registry.addProject(root.getName());

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
	
	private static interface FileSystemObserver {
		void onSettings( File settings );
		void onManifest( File manifest );
		void onBuild( File build );
	}
}
