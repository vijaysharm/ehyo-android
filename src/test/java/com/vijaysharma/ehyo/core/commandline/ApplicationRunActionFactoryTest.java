package com.vijaysharma.ehyo.core.commandline;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.RunActionBuilder;
import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactory.RunActionBuilderFactory;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverter;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverter;

public class ApplicationRunActionFactoryTest {
	private DirectoryCommandLineConverter directory;
	private PluginsCommandLineConverter plugins;
	private RunActionBuilderFactory factory;
	
	@Before
	public void before() {
		directory = mock(DirectoryCommandLineConverter.class);
		plugins = mock(PluginsCommandLineConverter.class);
		factory = mock(RunActionBuilderFactory.class);
	}
	
	@Test
	public void configure_adds_help_command_line_option() {
		String[] args = {};
		OptionParser parser = mock(OptionParser.class);
		new ApplicationRunActionFactory(args, directory, plugins, factory).configure(parser);
		
		verify(parser, times(1)).accepts("help");
	}
	
	@Test
	public void configure_adds_dryrun_command_line_option() {
		String[] args = {};
		OptionParser parser = mock(OptionParser.class);
		new ApplicationRunActionFactory(args, directory, plugins, factory).configure(parser);
		
		verify(parser, times(1)).accepts("dry-run");
	}
	
	@Test
	public void configure_calls_directory_configure_command_line_option() {
		String[] args = {};
		OptionParser parser = mock(OptionParser.class);
		new ApplicationRunActionFactory(args, directory, plugins, factory).configure(parser);
		
		verify(directory, times(1)).configure(parser);
	}
	
	@Test
	public void configure_calls_plugins_configure_command_line_option() {
		String[] args = {};
		OptionParser parser = mock(OptionParser.class);
		new ApplicationRunActionFactory(args, directory, plugins, factory).configure(parser);
		
		verify(plugins, times(1)).configure(parser);
	}
	
	@Test
	public void getAction_calls_create_on_factory_with_args() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		
		new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		verify(factory, times(1)).create(args);
	}
	
	@Test
	public void getAction_non_null_action() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		
		Action action = mock(Action.class);
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(value.build()).thenReturn(action);
		when(factory.create(args)).thenReturn(value);
		Action actualAction = new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		assertEquals(action, actualAction);
	}
	
	@Test
	public void getAction_calls_setShowHelp_option_on_builder() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		verify(value, times(1)).setShowHelp(Mockito.anyBoolean());
	}
	
	@Test
	public void getAction_calls_setDirectory_option_on_builder() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		verify(value, times(1)).setDirectory(Mockito.any(File.class));
	}
	
	@Test
	public void getAction_calls_setDryrun_option_on_builder() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		verify(value, times(1)).setDryrun(Mockito.anyBoolean());
	}

	@Test
	public void getAction_calls_setPluginOptions_option_on_builder() {
		String[] args = {};
		OptionSet options = mock(OptionSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		new ApplicationRunActionFactory(args, directory, plugins, factory).getAction(options);
		verify(value, times(1)).setPluginOptions(Mockito.any(PluginOptions.class));
	}
}
