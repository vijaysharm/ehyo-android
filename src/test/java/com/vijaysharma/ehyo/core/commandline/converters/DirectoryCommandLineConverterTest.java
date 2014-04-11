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

import com.vijaysharma.ehyo.core.commandline.ArgumentOption;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

public class DirectoryCommandLineConverterTest {
	private ArgumentOption<File> file;
	private DirectoryCommandLineConverter converter;
	
	@Before
	public void before() {
		file = mock(ArgumentOption.class);
		converter = new DirectoryCommandLineConverter(file);
	}
	
	@Test
	public void configure_sets_plugin_command_line_option() {
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		verify(parser, times(1)).addOptions(file);
	}

	@Test
	public void read_sets_directory_with_command_line_option() {
		when(file.supports("--directory")).thenReturn(true);
		when(file.hasRequiredArg()).thenReturn(true);
		when(file.getRequiredArgType()).thenReturn(File.class);
		
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		String expectedDirectory = "/home/test";
		List<String> args = newArrayList("--directory", expectedDirectory);
		ParsedSet options = spy(parser.parse(args));
		File directory = converter.read(options);
		
		assertEquals(expectedDirectory, directory.getAbsolutePath());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void read_throws_when_no_argument_given_for_directory() {
		when(file.supports("--directory")).thenReturn(true);
		when(file.hasRequiredArg()).thenReturn(true);
		when(file.getRequiredArgType()).thenReturn(File.class);
		
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		List<String> args = newArrayList("--directory");
		ParsedSet options = spy(parser.parse(args));
		converter.read(options);
	}
	
	@Test
	public void read_sets_default_directory_with_no_command_line_option() {
		when(file.supports("--directory")).thenReturn(true);
		when(file.hasRequiredArg()).thenReturn(true);
		when(file.getRequiredArgType()).thenReturn(File.class);
		when(file.getDefaultArgValue()).thenReturn(new File("."));

		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		List<String> args = newArrayList();
		ParsedSet options = spy(parser.parse(args));
		File directory = converter.read(options);
		
		assertEquals(".", directory.getName());
	}
}
