package com.vijaysharma.ehyo.api;

import java.util.Collection;

public class Service {
	private final Collection<Plugin> plugins;
	private final ManifestActionFactory manifestFactory;

	public Service(Collection<Plugin> plugins, ManifestActionFactory manifestFactory ) {
		this.plugins = plugins;
		this.manifestFactory = manifestFactory;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	public ManifestAction createManifestAction() {
		return manifestFactory.create();
	}
}
