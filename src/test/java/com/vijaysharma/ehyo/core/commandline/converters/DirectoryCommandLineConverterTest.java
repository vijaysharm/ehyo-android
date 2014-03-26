package com.vijaysharma.ehyo.core.commandline.converters;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import junit.framework.Assert;

import org.junit.Test;

public class DirectoryCommandLineConverterTest {
	@Test
	public void configure_sets_plugin_command_line_option() {
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		verify(parser, times(1)).accepts("directory");
	}
	
	@Test
	public void read_sets_directory_with_command_line_option() {
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		String expectedDirectory = "/home/test";
		OptionSet options = spy(parser.parse("--directory", expectedDirectory));
		File directory = converter.read(options);
		
		Assert.assertEquals(expectedDirectory, directory.getAbsolutePath());
	}
	
	@Test(expected=OptionException.class)
	public void read_throws_when_no_argument_given_for_directory() {
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		OptionSet options = spy(parser.parse("--directory"));
		converter.read(options);
	}
	
	@Test
	public void read_sets_default_directory_with_no_command_line_option() {
		DirectoryCommandLineConverter converter = new DirectoryCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		OptionSet options = spy(parser.parse("test"));
		File directory = converter.read(options);
		
		Assert.assertEquals(".", directory.getName());
	}
	
}
