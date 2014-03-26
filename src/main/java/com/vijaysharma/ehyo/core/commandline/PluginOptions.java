package com.vijaysharma.ehyo.core.commandline;

import java.util.Set;

import com.google.common.base.Optional;

public class PluginOptions {
	private final Set<String> plugins;
	private final Optional<String> plugin;

	public PluginOptions(String plugin, Set<String> plugins) {
		this.plugin = Optional.fromNullable(plugin);
		this.plugins = plugins;
	}
	
	public Optional<String> getPlugin() {
		return plugin;
	}
	
	public Set<String> getPlugins() {
		return plugins;
	}
}
