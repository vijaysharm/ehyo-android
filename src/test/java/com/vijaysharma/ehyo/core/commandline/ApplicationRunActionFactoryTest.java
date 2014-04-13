package com.vijaysharma.ehyo.core.commandline;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.RunAction;
import com.vijaysharma.ehyo.core.RunActionBuilder;
import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactory.RunActionBuilderFactory;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverter;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverter;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationRunActionFactoryTest {
	private DirectoryCommandLineConverter directory;
	private PluginsCommandLineConverter plugins;
	private RunActionBuilderFactory factory;
	private ApplicationRunActionFactory app;
	
	@Captor
	private ArgumentCaptor<ArgumentOption<String>[]> captor;
	
	@Before
	public void before() {
		directory = mock(DirectoryCommandLineConverter.class);
		plugins = mock(PluginsCommandLineConverter.class);
		factory = mock(RunActionBuilderFactory.class);
		app = new ApplicationRunActionFactory(directory, plugins, factory);
	}
	
	@Test
	public void RunActionBuilderFactory_returns_instanceof_RunAction() {
		RunActionBuilderFactory factory = new RunActionBuilderFactory();
		RunActionBuilder builder = factory.create(null);
		Action action = builder.build();

		assertEquals(RunAction.class, action.getClass());
	}
	
	@Test
	public void configure_adds_help_command_line_option() {
		CommandLineParser parser = mock(CommandLineParser.class);
		app.configure(parser);
		
//		verify(parser, times(1)).addOptions(captor.capture());
//		List<ArgumentOption<String>[]> allValues = captor.getAllValues();
//		Assert.assertEquals(2, values.size());
//		Assert.assertEquals(true, value.supports("--help"));
	}
/*	
	@Test
	public void configure_adds_dryrun_command_line_option() {
		List<String> args = newArrayList();
		CommandLineParser parser = mock(CommandLineParser.class);
		app.configure(parser);
		
		verify(parser, times(1)).accepts("dry-run");
	}
	
	@Test
	public void configure_calls_directory_configure_command_line_option() {
		CommandLineParser parser = mock(CommandLineParser.class);
		app.configure(parser);
		
		verify(directory, times(1)).configure(parser);
	}
	
	@Test
	public void configure_calls_plugins_configure_command_line_option() {
		CommandLineParser parser = mock(CommandLineParser.class);
		app.configure(parser);
		
		verify(plugins, times(1)).configure(parser);
	}
	
	@Test
	public void getAction_calls_create_on_factory_with_args() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		
		app.getAction(options);
		verify(factory, times(1)).create(args);
	}
	
	@Test
	public void getAction_non_null_action() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		
		Action action = mock(Action.class);
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(value.build()).thenReturn(action);
		when(factory.create(args)).thenReturn(value);
		Action actualAction = app.getAction(options);
		assertEquals(action, actualAction);
	}
	
	@Test
	public void getAction_calls_setShowHelp_option_on_builder() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		app.getAction(options);
		verify(value, times(1)).setShowHelp(Mockito.anyBoolean());
	}
	
	@Test
	public void getAction_calls_setDirectory_option_on_builder() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		app.getAction(options);
		verify(value, times(1)).setDirectory(Mockito.any(File.class));
	}
	
	@Test
	public void getAction_calls_setDryrun_option_on_builder() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		app.getAction(options);
		verify(value, times(1)).setDryrun(Mockito.anyBoolean());
	}

	@Test
	public void getAction_calls_setPluginOptions_option_on_builder() {
		List<String> args = newArrayList();
		ParsedSet options = mock(ParsedSet.class);
		
		RunActionBuilder value = mock(RunActionBuilder.class);
		when(factory.create(args)).thenReturn(value);
		app.getAction(options);
		verify(value, times(1)).setPluginOptions(Mockito.any(PluginOptions.class));
	}
*/
}
