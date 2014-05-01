package com.vijaysharma.ehyo.core.commandline.converters;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

@RunWith(MockitoJUnitRunner.class)
public class PluginsCommandLineConverterTest {
	
	private  PluginsCommandLineConverter converter;
	
	@Captor
	private ArgumentCaptor<ArgumentOption<String>> captor;
	
	@Before
	public void before() {
		converter = new PluginsCommandLineConverter();
	}
	
	@Test
	public void configure_sets_plugin_command_line_option() {
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		verify(parser, times(1)).addOptions(captor.capture());
		ArgumentOption<String> value = captor.getValue();
		assertTrue(value.supports("--plugins"));
		assertEquals(String.class, value.getRequiredArgType());
		assertEquals("com.vijaysharma.ehyo", value.getDefaultArgValue());
	}
	
	@Test
	public void read_adds_default_namespace_if_argument_is_provided() {
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		String expectedNamespace = "com.vijaysharma.test";
		List<String> args = Lists.newArrayList("--plugins", expectedNamespace);
		ParsedSet options = spy(parser.parse(args));
		Set<String> actualPlugins = converter.read(options);
		
		assertEquals(2, actualPlugins.size());
		assertTrue(actualPlugins.contains(expectedNamespace));
		assertTrue(actualPlugins.contains("com.vijaysharma.ehyo"));
	}

	@Test
	public void read_sets_default_namespace_with_no_command_line_option() {
		CommandLineParser parser = spy(new CommandLineParser());
		converter.configure(parser);
		
		List<String> args = Lists.newArrayList("--test");
		ParsedSet options = spy(parser.parse(args));
		Set<String> actualPlugins = converter.read(options);
		
		assertEquals(1, actualPlugins.size());
		assertTrue(actualPlugins.contains("com.vijaysharma.ehyo"));
	}
}
