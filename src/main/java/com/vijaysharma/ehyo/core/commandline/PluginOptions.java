package com.vijaysharma.ehyo.core.commandline;

import java.util.Set;

import com.google.common.base.Optional;

public class PluginOptions {
	private final Set<String> plugins;
	private final String plugin;

	public PluginOptions(String plugin, Set<String> plugins) {
		this.plugin = plugin;
		this.plugins = plugins;
	}
	
	public String getPlugin() {
		return plugin;
	}
	
	public Set<String> getPlugins() {
		return plugins;
	}
}
