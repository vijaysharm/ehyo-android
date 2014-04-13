package com.vijaysharma.ehyo.core;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunActionTest {
	private PluginLoader pluginLoader;
	private ProjectRegistryLoader projectLoader;
	private PluginActionHandlerFactory factory;
	private OptionSelector<AndroidManifest> manifestSelector;
	private ManifestChangeManagerFactory manifestChangeFactory;
	private GradleBuildChangeManagerFactory buildChangeFactory;
	private TextOutput out;
	
	@Before
	public void before() {
		pluginLoader = mock(PluginLoader.class);
		projectLoader = mock(ProjectRegistryLoader.class);
		factory = mock(PluginActionHandlerFactory.class);
		manifestSelector = mock(OptionSelector.class);
		manifestChangeFactory = mock(ManifestChangeManagerFactory.class);
		buildChangeFactory = mock(GradleBuildChangeManagerFactory.class);
		out = mock(TextOutput.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_no_plugin_found() {
		List<String> args = newArrayList();
		String pluginName = "some-name";
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.<Plugin>absent());
		
		RunAction action = create(args);
		action.run();
	}
	
	@Test(expected=RuntimeException.class)
	public void run_throws_when_null_plugin_found() {
		List<String> args = null;
		String pluginName = "some-name";
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.<Plugin>absent());
		
		RunAction action = create(args);
		action.run();
	}
	
	@Test
	public void run_calls_confiure_and_execute_on_plugin() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		
		RunAction action = create(args);
		action.run();
		verify(pluginLoader, times(1)).findPlugin(pluginName);
		verify(plugin, times(1)).execute(Mockito.eq(args), any(Service.class));
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
	public void run_loads_project_registry() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		PluginAction pluginAction = mock(PluginAction.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);

		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		when(plugin.execute(Mockito.eq(args), any(Service.class)))
			.thenReturn(Arrays.asList(pluginAction));
		
		RunAction action = create(args);
		action.run();
		verify(projectLoader, times(1)).load();
		verify(registry, never()).getAllAndroidManifests();
	}

	@Test
	public void run_asks_users_to_select_manifest_when_ManifestAction_is_passed() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		InternalManifestAction pluginAction = mock(InternalManifestAction.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		ManifestChangeManager changeManager = mock(ManifestChangeManager.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		when(plugin.execute(Mockito.eq(args), any(Service.class)))
			.thenReturn(asList(pluginAction));
		when(manifestChangeFactory.create(Mockito.anyList()))
			.thenReturn(changeManager);
		
		RunAction action = create(args);
		action.run();
		
		verify(manifestSelector, times(1)).select(Mockito.anyList());
		verify(changeManager, times(1)).commit(false);
	}

	@Test
	public void run_does_not_execute_PluginHandler_if_not_found() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		InternalManifestAction pluginAction = mock(InternalManifestAction.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		AndroidManifest manifest = mock(AndroidManifest.class);
		ManifestChangeManager changeManager = mock(ManifestChangeManager.class);
		AndroidManifestDocument doc = mock(AndroidManifestDocument.class);
		ManifestActionHandler handler = mock(ManifestActionHandler.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		when(plugin.execute(Mockito.eq(args), any(Service.class)))
			.thenReturn(asList(pluginAction));
		when(manifestSelector.select(Mockito.anyList()))
			.thenReturn(Arrays.asList(manifest));
		when(manifest.asDocument()).thenReturn(doc);
		when(factory.createManifestActionHandler(pluginAction)).thenReturn(null);
		when(manifestChangeFactory.create(Mockito.anyList()))
			.thenReturn(changeManager);
	
		RunAction action = create(args);
		action.run();
		
		verify(changeManager, never()).apply(handler);
		verify(changeManager, times(1)).commit(false);
	}
	
	@Test
	public void run_executes_changer_handler_perform_when_PluginHandler_is_found() {
		String pluginName = "some-name";
		List<String> args = newArrayList(pluginName);
		Plugin plugin = mock(Plugin.class);
		InternalManifestAction pluginAction = mock(InternalManifestAction.class);
		ProjectRegistry registry = mock(ProjectRegistry.class);
		AndroidManifest manifest = mock(AndroidManifest.class);
		ManifestChangeManager changeManager = mock(ManifestChangeManager.class);
		AndroidManifestDocument doc = mock(AndroidManifestDocument.class);
		ManifestActionHandler handler = mock(ManifestActionHandler.class);
		
		when(pluginLoader.findPlugin(pluginName)).thenReturn(Optional.of(plugin));
		when(projectLoader.load()).thenReturn(registry);
		when(plugin.execute(Mockito.eq(args), any(Service.class)))
			.thenReturn(asList(pluginAction));
		when(manifestSelector.select(Mockito.anyList()))
			.thenReturn(Arrays.asList(manifest));
		when(manifest.asDocument()).thenReturn(doc);
		doReturn(handler).when(factory).createManifestActionHandler(pluginAction);
		when(manifestChangeFactory.create(Mockito.anyList()))
			.thenReturn(changeManager);
	
		RunAction action = create(args);
		action.run();
		
		verify(changeManager, times(1)).apply(handler);
		verify(changeManager, times(1)).commit(false);
	}

	private static List<PluginAction> asList(PluginAction...actions) {
		List<PluginAction> result = Lists.newArrayList();
		for( PluginAction action : actions ) {
			result.add(action);
		}
		
		return result;
	}
	
	private RunAction create(List<String> args) {
		return create(args, false, false);
	}

	private RunAction create(List<String> args, boolean help, boolean dryrun) {
		return new RunAction(args, 
							 pluginLoader, 
							 projectLoader, 
							 factory, 
							 manifestSelector,
							 manifestChangeFactory,
							 buildChangeFactory,
							 help, 
							 dryrun,
							 out);
	}
}
