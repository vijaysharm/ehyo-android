package com.vijaysharma.ehyo.core.commandline.converters;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class PluginsCommandLineConverterTest {

	@Test
	public void configure_sets_plugin_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		verify(parser, times(1)).accepts("plugin");
	}
	
	@Test
	public void configure_sets_plugins_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		verify(parser, times(1)).accepts("plugins");
	}
	
	@Test
	public void read_sets_plugins_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		String expectedNamespace = "com.vijaysharma.test";
		OptionSet options = spy(parser.parse("--plugins", expectedNamespace));
		PluginOptions plugin = converter.read(options);
		
		verify(parser, times(1)).accepts("plugins");
		verify(options, times(2)).valueOf(Mockito.any(OptionSpec.class));
		
		Set<String> actualPlugins = plugin.getPlugins();
		assertEquals(2, actualPlugins.size());
		assertTrue(actualPlugins.contains(expectedNamespace));
		assertTrue(actualPlugins.contains("com.vijaysharma.ehyo"));
	}
	
	@Test
	public void read_sets_default_with_no_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		OptionSet options = spy(parser.parse("tesr"));
		PluginOptions plugin = converter.read(options);
		
		Set<String> actualPlugins = plugin.getPlugins();
		assertEquals(1, actualPlugins.size());
		assertTrue(actualPlugins.contains("com.vijaysharma.ehyo"));
	}
	
	@Test
	public void read_sets_plugin_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		String expectedPluginName = "list";
		OptionSet options = spy(parser.parse("--plugin", expectedPluginName));
		PluginOptions plugin = converter.read(options);
		
		verify(parser, times(1)).accepts("plugin");
		verify(options, times(2)).valueOf(Mockito.any(OptionSpec.class));
		
		assertEquals(expectedPluginName, plugin.getPlugin());
	}
	
	@Test
	public void read_sets_null_plugin_with_no_command_line_option() {
		PluginsCommandLineConverter converter = new PluginsCommandLineConverter();
		OptionParser parser = spy(new OptionParser());
		converter.configure(parser);
		
		OptionSet options = spy(parser.parse("test"));
		PluginOptions plugin = converter.read(options);
		
		assertNull(plugin.getPlugin());
	}
}
