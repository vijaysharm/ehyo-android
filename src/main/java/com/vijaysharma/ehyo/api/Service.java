package com.vijaysharma.ehyo.api;

import java.util.Collection;
import java.util.List;

import com.vijaysharma.ehyo.api.ActionFactories.BuildActionFactory;
import com.vijaysharma.ehyo.api.ActionFactories.ManifestActionFactory;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.api.utils.OptionSelectorFactory;

public class Service {
	private final Collection<Plugin> plugins;
	private final List<ProjectBuild> projectBuilds;
	private final List<ProjectManifest> manifests;
	private final List<BuildConfiguration> configurations; 
	private final ManifestActionFactory manifestFactory;
	private final BuildActionFactory buildActionFactory;
	private final OptionSelectorFactory selectorFactory;
	
	public Service(Collection<Plugin> plugins, 
				   List<ProjectManifest> manifests,
				   List<ProjectBuild> projectBuilds,
				   List<BuildConfiguration> configurations,
				   ManifestActionFactory manifestFactory, 
				   BuildActionFactory buildActionFactory,
				   OptionSelectorFactory selectorFactory ) {
		this.plugins = plugins;
		this.manifestFactory = manifestFactory;
		this.buildActionFactory = buildActionFactory;
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
	
	public ManifestAction createManifestAction() {
		return manifestFactory.create();
	}
	
	public BuildAction createBuildAction() {
		return buildActionFactory.create();
	}
	
	public <T> OptionSelector<T> createSelector(Class<T> clazz) {
		return selectorFactory.create(clazz);
	}
}
