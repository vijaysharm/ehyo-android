package com.vijaysharma.ehyo.core;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import com.vijaysharma.ehyo.core.ProjectRegistryLoader.FileConstructorFactory;
import com.vijaysharma.ehyo.core.ProjectRegistryLoader.FileObserverProjectBuilder;

public class ProjectRegistryLoaderTest {
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	private FileObserverProjectBuilder builder;
	private FileConstructorFactory<FileObserverProjectBuilder> factory;
	private ProjectRegistryLoader loader;
	
	@Before
	public void before() {
		builder = mock(FileObserverProjectBuilder.class);
		factory = mock(FileConstructorFactory.class);
		loader = new ProjectRegistryLoader(factory);
	}
	
	@Test
	public void load_doesnt_call_observe_with_non_directory_root() {
		File file = newFile("AndroidManifest.xml");
		when(factory.create(FileObserverProjectBuilder.class, file)).thenReturn(builder);
		
		loader.load(file);
		
		verify(builder, never()).onBuild(any(File.class));
		verify(builder, never()).onManifest(any(File.class));
		verify(builder, never()).onSettings(any(File.class));
		verify(builder, times(1)).build();
	}
	
	@Test
	public void load_doesnt_call_observe_with_empty_directory_root() {
		File folder = newFolder("root");
		when(factory.create(FileObserverProjectBuilder.class, folder)).thenReturn(builder);
		
		loader.load(folder);
		
		verify(builder, never()).onBuild(any(File.class));
		verify(builder, never()).onManifest(any(File.class));
		verify(builder, never()).onSettings(any(File.class));
		verify(builder, times(1)).build();
	}
	
	@Test
	public void load_finds_AndroidManifest() {
		File root = newFolder("root");
		File manifest = newFile("root/AndroidManifest.xml");
		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		
		when(factory.create(FileObserverProjectBuilder.class, root)).thenReturn(builder);
		loader.load(root);
		
		verify(builder, never()).onBuild(any(File.class));
		verify(builder, times(1)).onManifest(captor.capture());
		verify(builder, never()).onSettings(any(File.class));
		verify(builder, times(1)).build();
		
		File value = captor.getValue();
		assertEquals(manifest, value);
	}
	
	@Test
	public void load_finds_GradleSettings() {
		File root = newFolder("root");
		File settings = newFile("root/settings.gradle");
		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		
		when(factory.create(FileObserverProjectBuilder.class, root)).thenReturn(builder);
		loader.load(root);
		
		verify(builder, never()).onBuild(any(File.class));
		verify(builder, never()).onManifest(any(File.class));
		verify(builder, times(1)).onSettings(captor.capture());
		verify(builder, times(1)).build();
		
		File value = captor.getValue();
		assertEquals(settings, value);
	}
	
	@Test
	public void load_finds_GradleBuilds() {
		File root = newFolder("root");
		File build = newFile("root/build.gradle");
		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		
		when(factory.create(FileObserverProjectBuilder.class, root)).thenReturn(builder);
		loader.load(root);
		
		verify(builder, times(1)).onBuild(captor.capture());
		verify(builder, never()).onManifest(any(File.class));
		verify(builder, never()).onSettings(any(File.class));
		verify(builder, times(1)).build();
		
		File value = captor.getValue();
		assertEquals(build, value);
	}
	
	@Test
	public void load_skips_files_in_build_directory() {
		File root = newFolder("root");
		newFolder("root/build");
		newFile("root/build/build.gradle");
		newFile("root/build/settings.gradle");
		newFile("root/build/AndroidManifest.xml");
		
		when(factory.create(FileObserverProjectBuilder.class, root)).thenReturn(builder);
		loader.load(root);
		
		verify(builder, never()).onBuild(any(File.class));
		verify(builder, never()).onManifest(any(File.class));
		verify(builder, never()).onSettings(any(File.class));
		verify(builder, times(1)).build();
	}
	
	@Test
	public void load_finds_files_in_deep_directories() {
		File root = newFolder("root");
		newFolder("root/src");
		File build = newFile("root/src/build.gradle");
		File settings = newFile("root/src/settings.gradle");
		File manifest = newFile("root/src/AndroidManifest.xml");
		
		when(factory.create(FileObserverProjectBuilder.class, root)).thenReturn(builder);
		loader.load(root);
		
		verify(builder, times(1)).onBuild(eq(build));
		verify(builder, times(1)).onManifest(eq(manifest));
		verify(builder, times(1)).onSettings(eq(settings));
		verify(builder, times(1)).build();
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
