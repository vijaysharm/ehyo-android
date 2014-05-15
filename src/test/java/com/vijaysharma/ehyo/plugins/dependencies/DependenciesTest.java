package com.vijaysharma.ehyo.plugins.dependencies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.MavenService;
import com.vijaysharma.ehyo.api.QueryByNameResponse;
import com.vijaysharma.ehyo.api.QueryResults;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.UsageException;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class DependenciesTest {
	private Dependencies dependencies;
	private Service service;
	private MavenService mavenService;
	private Output out;
	
	@Captor
	ArgumentCaptor<Set<Artifact>> captor;
	
	@Before
	public void before() {
		service = mock(Service.class);
		mavenService = mock(MavenService.class);
		out = mock(Output.class);

		when(service.getMavenService()).thenReturn(mavenService);
		
		dependencies = new Dependencies(out);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_args_is_empty() {
		List<String> args = Lists.newArrayList();
		dependencies.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_search_or_remove_are_not_given() {
		List<String> args = Lists.newArrayList("--test");
		dependencies.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_s_has_no_arguments() {
		List<String> args = Lists.newArrayList("-s");
		dependencies.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_s_and_g_are_provided() {
		List<String> args = Lists.newArrayList("-s", "-g");
		dependencies.execute(args, service);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_search_by_name_finds_nothing() {
		String library = "library";
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[0]);
		
		List<String> args = Lists.newArrayList("-s", library);
		dependencies.execute(args, service);
	}
	
	@Test
	public void execute_prints_artifacts() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		
		List<String> args = Lists.newArrayList("-s", library);
		dependencies.execute(args, service);
		
		ArgumentCaptor<Object> output = ArgumentCaptor.forClass(Object.class);
		verify(out, times(1)).println(output.capture());
		
		Object value = output.getValue();
		assertEquals("Found the following artifacts:\n" + 
					 "    com.android.tools.build:gradle:0.7.+\n" +
					 "Append --add to this command to add the dependency to your build.", value);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_if_all_configs_have_the_found_artifact() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(Sets.newHashSet(artifact));
		
		List<String> args = Lists.newArrayList("-s", library, "--add");
		dependencies.execute(args, service);
	}
	
	@Test
	public void execute_can_add_artifacts_when_build_has_no_dependencies() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		Set<Artifact> emptyArtifacts = Sets.newHashSet();
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listOfConfigs);
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(emptyArtifacts);
		
		List<String> args = Lists.newArrayList("-s", library, "--add");
		dependencies.execute(args, service);
		
		ArgumentCaptor<Artifact> artifactCaptor = ArgumentCaptor.forClass(Artifact.class);
		verify(config).addArtifact(artifactCaptor.capture());
		assertEquals(artifact, artifactCaptor.getValue());
	}
	
	@Test
	public void execute_can_add_artifacts() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		Set<Artifact> emptyArtifacts = Sets.newHashSet(
			Artifact.read("com.android.tools.build:buttknife:0.7.1")
		);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listOfConfigs);
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(emptyArtifacts);
		
		List<String> args = Lists.newArrayList("-s", library, "--add");
		dependencies.execute(args, service);
		
		ArgumentCaptor<Artifact> artifactCaptor = ArgumentCaptor.forClass(Artifact.class);
		verify(config).addArtifact(artifactCaptor.capture());
		assertEquals(artifact, artifactCaptor.getValue());
	}
	
	@Test
	public void execute_can_update_artifacts() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		Artifact old = Artifact.read("com.android.tools.build:gradle:0.7.0");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		OptionSelector<String> yesnoSelector = mock(OptionSelector.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		List<BuildConfiguration> selectedConfigs = Lists.newArrayList(config);
		Set<Artifact> artifacts = Sets.newHashSet(old);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(selectedConfigs);
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(yesnoSelector);
		when(yesnoSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList("Yes - Switch gradle to 0.7.+"));
		
		when(config.getArtifacts()).thenReturn(artifacts);
		
		List<String> args = Lists.newArrayList("-s", library, "--add");
		dependencies.execute(args, service);
		
		ArgumentCaptor<Artifact> artifactCaptor = ArgumentCaptor.forClass(Artifact.class);
		verify(config).addArtifact(artifactCaptor.capture());
		assertEquals(artifact, artifactCaptor.getValue());
		
		ArgumentCaptor<Artifact> removed = ArgumentCaptor.forClass(Artifact.class);
		verify(config).removeArtifact(removed.capture());
		assertEquals(old, removed.getValue());
	}
	
	@Test
	public void execute_wont_upgrade_if_user_doesnt_want_to() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		Artifact old = Artifact.read("com.android.tools.build:gradle:0.7.0");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		OptionSelector<String> yesnoSelector = mock(OptionSelector.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		List<BuildConfiguration> selectedConfigs = Lists.newArrayList(config);
		Set<Artifact> artifacts = Sets.newHashSet(old);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(selectedConfigs);
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(yesnoSelector);
		when(yesnoSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList("No"));
		
		when(config.getArtifacts()).thenReturn(artifacts);
		
		List<String> args = Lists.newArrayList("-s", library, "--add");
		dependencies.execute(args, service);
		
		verify(config, never()).addArtifact(Mockito.any(Artifact.class));
		verify(config, never()).addArtifacts(Mockito.any(Set.class));
		verify(config, never()).removeArtifact(Mockito.any(Artifact.class));
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_removing_from_all_dependencies_and_none_are_found() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		Set<Artifact> empty = Sets.newHashSet();
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(empty);
		
		List<String> args = Lists.newArrayList("--remove");
		dependencies.execute(args, service);
	}
	
	@Test
	public void execute_removes_depency() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(Sets.newHashSet(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listOfConfigs);

		List<String> args = Lists.newArrayList("--remove");
		dependencies.execute(args, service);
		
		verify(config).removeArtifacts(captor.capture());
		Set<Artifact> value = captor.getValue();
		assertEquals(1, value.size());
		assertEquals(true, value.contains(artifact));
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_removing_from_searched_dependencies_and_none_are_found() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		Set<Artifact> empty = Sets.newHashSet(
			Artifact.read("com.jakewharton:butterknife:5.0.0")
		);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(empty);
		
		List<String> args = Lists.newArrayList("--remove", "blah");
		dependencies.execute(args, service);
	}
	
	@Test
	public void execute_removes_depency_with_arg() {
		String library = "library";
		Artifact artifact = Artifact.read("com.android.tools.build:gradle:0.7.+");
		QueryByNameResponse response = mock(QueryByNameResponse.class);
		QueryResults results = mock(QueryResults.class);
		BuildConfiguration config = mock(BuildConfiguration.class);
		List<BuildConfiguration> listOfConfigs = Lists.newArrayList(config);
		OptionSelector<Artifact> artifactSelector = mock(OptionSelector.class);
		OptionSelector<BuildConfiguration> buildSelector = mock(OptionSelector.class);
		
		when(mavenService.searchByName(library)).thenReturn(response);
		when(response.getResponse()).thenReturn(results);
		when(results.getArtifacts()).thenReturn(new Artifact[]{ artifact });
		when(service.getBuildConfigurations()).thenReturn(listOfConfigs);
		when(config.getArtifacts()).thenReturn(Sets.newHashSet(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(Artifact.class))).thenReturn(artifactSelector);
		when(artifactSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(Lists.newArrayList(artifact));
		when(service.createSelector(Mockito.anyString(), Mockito.eq(BuildConfiguration.class))).thenReturn(buildSelector);
		when(buildSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listOfConfigs);

		List<String> args = Lists.newArrayList("--remove", "android");
		dependencies.execute(args, service);
		
		verify(config).removeArtifacts(captor.capture());
		Set<Artifact> value = captor.getValue();
		assertEquals(1, value.size());
		assertEquals(true, value.contains(artifact));
	}
}
