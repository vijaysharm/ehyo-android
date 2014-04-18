package com.vijaysharma.ehyo.api.plugins.search;

import static com.google.common.base.Joiner.on;

import java.util.List;

import retrofit.RestAdapter;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.plugins.search.models.Artifact;
import com.vijaysharma.ehyo.api.plugins.search.models.QueryByNameResponse;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class SearchLibraryPlugin implements Plugin {

	@Override
	public String name() {
		return "search-mvn-central";
	}

	/**
	 * TODO: Should support searching by artifact (i.e. distinguish between
	 * 'com.netflix.rxjava:rxjava-core:0.16.1' and
	 * 'com.netflix.rxjava:rxjava-android:0.16.1')
	 */
	@Override
	public void execute(List<String> args, Service service) {
		String searchValue = getLib(args);
		Output.out.println("Searching: " + searchValue);

		if ( searchValue != null ) {
			RestAdapter restAdapter = new RestAdapter.Builder()
		    	.setEndpoint("http://search.maven.org")
		    	.build();
			
			MavenService maven = restAdapter.create(MavenService.class);
			QueryByNameResponse name = maven.searchByName(searchValue);
			Artifact[] artifacts = name.getResponse().getArtifacts();
			if ( artifacts.length == 0 ) {
				Output.err.println(searchValue + " was not found in the remote maven repository");
				return;
			}
	
			Artifact first = artifacts[0];
			
			List<BuildConfiguration> buildConfigs = gather(service);
			OptionSelector<BuildConfiguration> configSelector = service.createSelector(BuildConfiguration.class);
			List<BuildConfiguration> selectedBuildConfigs = configSelector.select(buildConfigs, false);

			for ( BuildConfiguration config : selectedBuildConfigs ) {
				config.addDependency(toProjectId(first));
			}
		}
	}

	private List<BuildConfiguration> gather(Service service) {
		ImmutableList.Builder<BuildConfiguration> configs = ImmutableList.builder();
		for( ProjectBuild build : service.getProjectBuilds() ) {
			configs.addAll(build.getBuildConfigurations());
		}

		return configs.build();
	}

	private static String getLib(List<String> args) {
		int libIndex = args.indexOf("--lib");
		
		if (libIndex == -1) {
			return null;
		}
		
		int libValueIndex = libIndex + 1;
		if ( libValueIndex >= args.size() )
			return null;
		
		return args.get(libValueIndex);
	}
	
	private String toProjectId(Artifact artifact) {
		return on(":").join(artifact.getId(), artifact.getLatestVersion());
	}
}
