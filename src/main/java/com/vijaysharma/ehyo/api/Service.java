package com.vijaysharma.ehyo.api;

import java.util.Collection;
import java.util.List;

import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class Service {
	private final Collection<Plugin> plugins;
	private final List<ProjectBuild> projectBuilds;
	private final List<ProjectManifest> manifests;
	private final List<BuildConfiguration> configurations; 
	private final OptionSelectorFactory selectorFactory;
	
	public Service(Collection<Plugin> plugins, 
				   List<ProjectManifest> manifests,
				   List<ProjectBuild> projectBuilds,
				   List<BuildConfiguration> configurations,
				   OptionSelectorFactory selectorFactory) {
		this.plugins = plugins;
		this.projectBuilds = projectBuilds;
		this.manifests = manifests;
		this.selectorFactory = selectorFactory;
		this.configurations = configurations;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	public List<ProjectBuild> getProjectBuilds() {
		return projectBuilds;
	}

	public List<ProjectManifest> getManifests() {
		return manifests;
	}
	
	public List<BuildConfiguration> getConfigurations() {
		return configurations;
	}
	
	public <T> OptionSelector<T> createSelector(Class<T> clazz) {
		return selectorFactory.create(clazz);
	}
}
