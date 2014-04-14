package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.ProjectRegistryLoader.FileConstructorFactory;
import com.vijaysharma.ehyo.core.ProjectRegistryLoader.FileObserverProjectBuilder;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;

public class FileObserverProjectBuilderTest {
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	private FileConstructorFactory<ProjectRegistryBuilder> factory;
	private ProjectRegistryBuilder registry;
	
	@Before
	public void before() {
		factory = mock(FileConstructorFactory.class);
		registry = mock(ProjectRegistryBuilder.class);
	}
	
	@Test
	public void build_adds_root_to_registry() {
		File root = newFolder("root");
		when(factory.create(ProjectRegistryBuilder.class, root)).thenReturn(registry);
		FileObserverProjectBuilder builder = create(root);

		builder.build();
		
		verify(registry, times(1)).addProject(root.getName());
		verify(registry, times(0)).addProjects(Mockito.anyList());
		verify(registry, times(0)).addBuild(any(GradleBuild.class));
		verify(registry, times(0)).adddManifest(any(AndroidManifest.class));
		verify(registry, times(1)).build();
	}
	
	private FileObserverProjectBuilder create( File root ) {
		return new FileObserverProjectBuilder(root, factory);
	}

	private File newFile(String name) {
		try {
			return folder.newFile(name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private File newFolder(String name) {
		return folder.newFolder(name);
	}
}