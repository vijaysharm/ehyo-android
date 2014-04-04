package com.vijaysharma.ehyo.core.commandline;

import java.util.Set;

public class PluginOptions {
	private final Set<String> namespaces;
	private final String plugin;

	public PluginOptions(String plugin, Set<String> namespaces) {
		this.plugin = plugin;
		this.namespaces = namespaces;
	}
	
	public String getPlugin() {
		return plugin;
	}
	
	public Set<String> getPluginNamespaces() {
		return namespaces;
	}
}
