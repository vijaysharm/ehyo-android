package com.vijaysharma.ehyo.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.ProjectRegistryLoader.FileObserverProjectBuilder;

public class FileObserverProjectBuilderTest {
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	private ObjectFactory factory;
	private ProjectRegistryBuilder registry;
	private FileObserverProjectBuilder builder;
	
	@Before
	public void before() {
		factory = mock(ObjectFactory.class);
		registry = mock(ProjectRegistryBuilder.class);
		builder = new FileObserverProjectBuilder(factory);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void build_does_not_add_root_to_registry() {
		File root = newFolder("root");
		when(factory.create(ProjectRegistryBuilder.class)).thenReturn(registry);

		builder.build();
		
		verify(registry, times(0)).addProject(root.getName());
		verify(registry, times(0)).addProjects(Mockito.anyList());
		verify(registry, times(0)).addBuild(any(File.class));
		verify(registry, times(0)).adddManifest(any(File.class));
		verify(registry, times(1)).build();
	}
	
	private File newFolder(String name) {
		return folder.newFolder(name);
	}
}
