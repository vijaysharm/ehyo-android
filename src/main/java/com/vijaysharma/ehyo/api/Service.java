package com.vijaysharma.ehyo.api;

import java.util.Collection;
import java.util.List;

import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class Service {
	private final Collection<Plugin> plugins;
	private final List<ProjectBuild> projectBuilds;
	private final List<ProjectManifest> manifests;
	private final OptionSelectorFactory selectorFactory;
	
	public Service(Collection<Plugin> plugins, 
				   List<ProjectManifest> manifests,
				   List<ProjectBuild> projectBuilds,
				   OptionSelectorFactory selectorFactory) {
		this.plugins = plugins;
		this.projectBuilds = projectBuilds;
		this.manifests = manifests;
		this.selectorFactory = selectorFactory;
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
	
	public <T> OptionSelector<T> createSelector(Class<T> clazz) {
		return selectorFactory.create(clazz);
	}
}
