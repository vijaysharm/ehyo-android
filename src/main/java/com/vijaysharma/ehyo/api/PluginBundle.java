package com.vijaysharma.ehyo.api;

import java.util.Collection;

public class PluginBundle {
	private Collection<Plugin> plugins;

	public PluginBundle(Collection<Plugin> plugins) {
		this.plugins = plugins;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}
}
