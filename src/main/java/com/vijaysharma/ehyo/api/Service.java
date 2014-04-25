package com.vijaysharma.ehyo.api;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class Service {
	private final Collection<Plugin> plugins;
	private final List<ProjectBuild> projectBuilds;
	private final OptionSelectorFactory selectorFactory;
	
	public Service(Collection<Plugin> plugins, 
				   List<ProjectBuild> projectBuilds,
				   OptionSelectorFactory selectorFactory) {
		this.plugins = plugins;
		this.projectBuilds = projectBuilds;
		this.selectorFactory = selectorFactory;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	public List<ProjectBuild> getProjectBuilds() {
		return projectBuilds;
	}
	
	public <T> OptionSelector<T> createSelector(Class<T> clazz) {
		return selectorFactory.create(clazz);
	}

	public List<ProjectManifest> getManifests() {
		ImmutableList.Builder<ProjectManifest> manifests = ImmutableList.builder();
		for ( ProjectBuild build : getProjectBuilds() ) {
			manifests.addAll(build.getManifests());
		}

		return manifests.build();
	}
	
	public List<BuildConfiguration> getBuildConfigurations() {
		ImmutableList.Builder<BuildConfiguration> configs = ImmutableList.builder();
		for( ProjectBuild build : getProjectBuilds() ) {
			configs.addAll(build.getBuildConfigurations());
		}

		return configs.build();
	}	
}
