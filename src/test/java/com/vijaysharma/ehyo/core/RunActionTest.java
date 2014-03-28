package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunActionTest {
	private PluginLoader loader;
	private PluginOptions pluginOptions;
	private ProjectRegistryLoader projectLoader;
	
	@Before
	public void before() {
		loader = mock(PluginLoader.class);
		pluginOptions = mock(PluginOptions.class);
		projectLoader = mock(ProjectRegistryLoader.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_no_plugin_found() {
		String[] args = {};
		String pluginName = "some-name";
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(loader.findPlugin(pluginName)).thenReturn(Optional.<Plugin>absent());
		
		RunAction action = create(args);
		action.run();
	}
	
	@Test
	public void run_calls_confiure_and_execute_on_plugin() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(loader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		
		RunAction action = create(args);
		action.run();
		verify(loader, times(1)).findPlugin(pluginName);
		verify(plugin, times(1)).configure(Mockito.any(OptionParser.class));
		verify(plugin, times(1)).execute(Mockito.any(OptionSet.class), Mockito.any(Service.class));
	}
	
	@Test
	public void run_calls_doesnt_call_execute_for_help_on_plugin() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(loader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		
		RunAction action = create(args, true, false);
		action.run();
		verify(loader, times(1)).findPlugin(pluginName);
		verify(plugin, times(1)).configure(Mockito.any(OptionParser.class));
		verify(plugin, times(0)).execute(Mockito.any(OptionSet.class), Mockito.any(Service.class));
	}
	
	private RunAction create(String[] args) {
		return create(args, false, false);
	}

	private RunAction create(String[] args, boolean help, boolean dryrun) {
		return new RunAction(args, pluginOptions, loader, projectLoader, help, dryrun);
	}
}
