package com.vijaysharma.ehyo.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunActionTest {
	private PluginLoader pluginLoader;
	private PluginOptions pluginOptions;
	private ProjectRegistryLoader projectLoader;
	private PluginActionHandlerFactory factory;
	private FileSelector<AndroidManifest> manifestSelector;
	
	@Before
	public void before() {
		pluginLoader = mock(PluginLoader.class);
		pluginOptions = mock(PluginOptions.class);
		projectLoader = mock(ProjectRegistryLoader.class);
		factory = mock(PluginActionHandlerFactory.class);
		manifestSelector = mock(FileSelector.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_no_plugin_found() {
		String[] args = {};
		String pluginName = "some-name";
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.<Plugin>absent());
		
		RunAction action = create(args);
		action.run();
	}
	
	@Test
	public void run_calls_confiure_and_execute_on_plugin() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		
		RunAction action = create(args);
		action.run();
		verify(pluginLoader, times(1)).findPlugin(pluginName);
		verify(plugin, times(1)).configure(any(OptionParser.class));
		verify(plugin, times(1)).execute(any(OptionSet.class), any(Service.class));
	}
	
	@Test
	public void run_doesnt_call_execute_for_help_on_plugin() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		
		RunAction action = create(args, true, false);
		action.run();
		verify(pluginLoader, times(1)).findPlugin(pluginName);
		verify(plugin, times(1)).configure(any(OptionParser.class));
		verify(plugin, times(0)).execute(any(OptionSet.class), any(Service.class));
	}
	
	@Test
	public void run_loads_project_registry() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		PluginAction pluginAction = mock(PluginAction.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		when(plugin.execute(any(OptionSet.class), any(Service.class)))
			.thenReturn(Arrays.asList(pluginAction));
		
		RunAction action = create(args);
		action.run();
		verify(projectLoader, times(1)).load();
		verify(registry, never()).getAllAndroidManifests();
	}
	
	public void run_asks_users_to_select_manifest_when_ManifestAction_is_passed() {
		String[] args = {};
		String pluginName = "some-name";
		Plugin plugin = mock(Plugin.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		when(pluginOptions.getPlugin()).thenReturn(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		
		RunAction action = create(args);
		action.run();
//		verify(projectLoader, times(1)).load();
//		verify(registry, never()).getAllAndroidManifests();
	}
	
	private RunAction create(String[] args) {
		return create(args, false, false);
	}

	private RunAction create(String[] args, boolean help, boolean dryrun) {
		return new RunAction(args, 
							 pluginOptions, 
							 pluginLoader, 
							 projectLoader, 
							 factory, 
							 manifestSelector, 
							 help, 
							 dryrun);
	}
}
