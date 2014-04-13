package com.vijaysharma.ehyo.core.commandline.converters;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
	private ProjectRegistry registry;
	
	private ArgumentCaptor<File> captor;
	
	@Before
	public void before() {
		option = mock(ArgumentOption.class);
		factory = mock(ProjectRegistryLoaderFactory.class);
		captor = ArgumentCaptor.forClass(File.class);
		loader = mock(ProjectRegistryLoader.class);
		registry = mock(ProjectRegistry.class);
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
		when(optionSet.value(option)).thenReturn(file);
		when(factory.create(file)).thenReturn(loader);
		
		converter.read(optionSet);
		verify(factory, times(1)).create(file);
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
