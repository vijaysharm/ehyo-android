package com.vijaysharma.ehyo.api;

import java.util.Collection;

import com.vijaysharma.ehyo.api.ActionFactories.BuildActionFactory;
import com.vijaysharma.ehyo.api.ActionFactories.ManifestActionFactory;

public class Service {
	private final Collection<Plugin> plugins;
	private final ManifestActionFactory manifestFactory;
	private final BuildActionFactory buildActionFactory;
	
	public Service(Collection<Plugin> plugins, 
				   ManifestActionFactory manifestFactory,
				   BuildActionFactory buildActionFactory) {
		this.plugins = plugins;
		this.manifestFactory = manifestFactory;
		this.buildActionFactory = buildActionFactory;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	public ManifestAction createManifestAction() {
		return manifestFactory.create();
	}
	
	public BuildAction createBuildAction() {
		return buildActionFactory.create();
	}
}
