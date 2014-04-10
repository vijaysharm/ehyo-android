package com.vijaysharma.ehyo.api.plugins.search;

import static com.google.common.base.Joiner.on;

import java.util.ArrayList;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import retrofit.RestAdapter;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.api.plugins.search.models.Artifact;
import com.vijaysharma.ehyo.api.plugins.search.models.QueryByNameResponse;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class SearchLibraryPlugin implements Plugin {
	private ArgumentAcceptingOptionSpec<String> lib;

	@Override
	public String name() {
		return "search-mvn-central";
	}

	@Override
	public void configure(OptionParser parser) {
		this.lib = parser.accepts("lib")
				.withOptionalArg()
				.ofType(String.class);
	}

	/**
	 * TODO: Should support searching by artifact (i.e. distinguish between
	 * 'com.netflix.rxjava:rxjava-core:0.16.1' and
	 * 'com.netflix.rxjava:rxjava-android:0.16.1')
	 */
	@Override
	public List<PluginAction> execute(OptionSet options, Service service) {
		ArrayList<PluginAction> actions = Lists.newArrayList();
		
		if ( options.has(lib) ) {
			String searchValue = lib.value(options);
			
			RestAdapter restAdapter = new RestAdapter.Builder()
		    	.setEndpoint("http://search.maven.org")
		    	.build();
			
			MavenService maven = restAdapter.create(MavenService.class);
			QueryByNameResponse name = maven.searchByName(searchValue);
			Artifact[] artifacts = name.getResponse().getArtifacts();
			if ( artifacts.length == 0 ) {
				Outputter.err.println(searchValue + " was not found in the remote maven repository");
				return Lists.newArrayList();
			}
	
			Artifact first = artifacts[0];
			
			List<BuildConfiguration> buildConfigs = service.getConfigurations();
			OptionSelector<BuildConfiguration> configSelector = service.createSelector(BuildConfiguration.class);
			List<BuildConfiguration> selectedBuildConfigs = configSelector.select(buildConfigs, false);
			
			BuildAction action = service.createBuildAction();
			for ( BuildConfiguration config : selectedBuildConfigs ) {
				action.addDependency(config, toProjectId(first));
			}
			actions.add(action);
		}
		
		return actions;
	}

	private String toProjectId(Artifact artifact) {
		return on(":").join(artifact.getId(), artifact.getLatestVersion());
	}
}
