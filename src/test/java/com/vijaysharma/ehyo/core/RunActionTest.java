package com.vijaysharma.ehyo.core;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.FileChangeManager.FileChangeManagerFactory;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunActionTest {
	private PluginLoader pluginLoader;
	private ProjectRegistry registry;
	private ManifestChangeManagerFactory manifestChangeFactory;
	private GradleBuildChangeManagerFactory buildChangeFactory;
	private FileChangeManagerFactory fileChangeManagerFactory;
	private ServiceFactory serviceFactory;
	private ObjectFactory<PluginActions> actionsFactory;
	private TextOutput out;
	
	@Before
	public void before() {
		pluginLoader = mock(PluginLoader.class);
		registry = mock(ProjectRegistry.class);
		manifestChangeFactory = mock(ManifestChangeManagerFactory.class);
		buildChangeFactory = mock(GradleBuildChangeManagerFactory.class);
		fileChangeManagerFactory = mock(FileChangeManagerFactory.class);
		serviceFactory = mock(ServiceFactory.class);
		actionsFactory = mock(ObjectFactory.class);
		out = mock(TextOutput.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_args_are_null() {
		List<String> args = newArrayList();
		RunAction action = create(args);
		action.run();
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_args_are_empty() {
		List<String> args = newArrayList();
		RunAction action = create(args);
		action.run();
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_no_plugin_found() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.<Plugin>absent());
		
		RunAction action = create(args);
		action.run();
	}
	
	@Test
	public void run_calls_create_on_PluginActionsFactory() {
		Plugin plugin = mock(Plugin.class);
		PluginActions actions = mock(PluginActions.class);
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(plugin.name()).thenReturn(pluginName);
		when(actionsFactory.create()).thenReturn(actions);
		
		RunAction action = create(args);
		action.run();
		
		verify(actionsFactory, times(1)).create();
	}
	
	@Test
	public void run_calls_create_on_ServiceFactory() {
		Plugin plugin = mock(Plugin.class);
		PluginActions actions = mock(PluginActions.class);
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(plugin.name()).thenReturn(pluginName);
		when(actionsFactory.create()).thenReturn(actions);
		
		RunAction action = create(args);
		action.run();
		
		verify(serviceFactory, times(1)).create(pluginLoader, registry, actions);
	}
	
	@Test
	public void run_calls_execute_on_plugin() {
		Plugin plugin = mock(Plugin.class);
		PluginActions actions = mock(PluginActions.class);
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Service service = mock(Service.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(plugin.name()).thenReturn(pluginName);
		when(actionsFactory.create()).thenReturn(actions);
		when(serviceFactory.create(pluginLoader, registry, actions)).thenReturn(service);
		
		RunAction action = create(args);
		action.run();
		
		verify(plugin, times(1)).execute(Mockito.anyList(), Mockito.eq(service));
	}

	@Test
	public void run_doesnt_call_execute_for_help_on_plugin() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		
		RunAction action = create(args, true, false);
		action.run();
		verify(pluginLoader, times(1)).findPlugin(pluginName);
		verify(plugin, times(0)).execute(Mockito.eq(args), any(Service.class));
	}
	
	@Test
	public void run_creates_manifest_change_manager_if_changes_exists() {
		Plugin plugin = mock(Plugin.class);
		PluginActions actions = mock(PluginActions.class);
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		ManifestChangeManager changes = mock(ManifestChangeManager.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(plugin.name()).thenReturn(pluginName);
		when(actionsFactory.create()).thenReturn(actions);
		when(actions.hasManifestChanges()).thenReturn(true);
		when(manifestChangeFactory.create()).thenReturn(changes);
		
		RunAction action = create(args);
		action.run();
		
		verify(changes, times(1)).apply(Mockito.eq(actions));
		verify(changes, times(1)).commit(Mockito.eq(false));
	}
	
	@Test
	public void run_creates_build_change_manager_if_changes_exists() {
		Plugin plugin = mock(Plugin.class);
		PluginActions actions = mock(PluginActions.class);
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		GradleBuildChangeManager changes = mock(GradleBuildChangeManager.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(plugin.name()).thenReturn(pluginName);
		when(actionsFactory.create()).thenReturn(actions);
		when(actions.hasBuildChanges()).thenReturn(true);
		when(buildChangeFactory.create()).thenReturn(changes);
		
		RunAction action = create(args);
		action.run();
		
		verify(changes, times(1)).apply(Mockito.eq(actions));
		verify(changes, times(1)).commit(Mockito.eq(false));
	}
	
	private RunAction create(List<String> args) {
		return create(args, false, false);
	}

	private RunAction create(List<String> args, boolean help, boolean dryrun) {
		return new RunAction(args, 
							 pluginLoader, 
							 registry, 
							 manifestChangeFactory,
							 buildChangeFactory,
							 fileChangeManagerFactory,
							 serviceFactory,
							 actionsFactory,
							 help, 
							 dryrun,
							 out);
	}
}
