package com.vijaysharma.ehyo.core.commandline.converters;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.core.ProjectRegistryLoader;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverter.ProjectRegistryLoaderFactory;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class DirectoryCommandLineConverterTest {
	private ArgumentOption<File> option;
	private DirectoryCommandLineConverter converter;
	private ProjectRegistryLoaderFactory factory;
	private ProjectRegistryLoader loader;
	
	@Before
	public void before() {
		option = mock(ArgumentOption.class);
		factory = mock(ProjectRegistryLoaderFactory.class);
		loader = mock(ProjectRegistryLoader.class);
		converter = new DirectoryCommandLineConverter(option, factory);
	}
	
	@Test
	public void configure_sets_plugin_command_line_option() {
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		verify(parser, times(1)).addOptions(option);
	}

	@Test
	public void read_calls_create_on_ProjectRegistryLoaderFactory() {
		ParsedSet optionSet = mock(ParsedSet.class);
		File file = mock(File.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		
		when(optionSet.value(option)).thenReturn(file);
		when(factory.create()).thenReturn(loader);
		when(loader.load(file)).thenReturn(registry);
		when(registry.isEmpty()).thenReturn(false);
		when(file.exists()).thenReturn(true);
		
		converter.read(optionSet);
		verify(factory, times(1)).create();
		verify(loader, times(1)).load(file);
	}
	
	@Test(expected=GentleMessageException.class)
	public void read_throws_GentleMessageException_when_registry_is_empty() {
		ParsedSet optionSet = mock(ParsedSet.class);
		File file = mock(File.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		
		when(optionSet.value(option)).thenReturn(file);
		when(factory.create()).thenReturn(loader);
		when(loader.load(file)).thenReturn(registry);
		when(registry.isEmpty()).thenReturn(true);
		
		converter.read(optionSet);
	}
	
	@Test(expected=GentleMessageException.class)
	public void read_throws_GentleMessageException_when_directory_path_does_not_exist() {
		ParsedSet optionSet = mock(ParsedSet.class);
		File file = mock(File.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		
		when(optionSet.value(option)).thenReturn(file);
		when(factory.create()).thenReturn(loader);
		when(loader.load(file)).thenReturn(registry);
		when(registry.isEmpty()).thenReturn(false);
		when(file.exists()).thenReturn(false);
		
		converter.read(optionSet);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void read_throws_when_no_argument_given_for_directory() {
		when(option.supports("--directory")).thenReturn(true);
		when(option.hasRequiredArg()).thenReturn(true);
		when(option.getRequiredArgType()).thenReturn(File.class);
		
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		List<String> args = newArrayList("--directory");
		parser.parse(args);
	}
}
