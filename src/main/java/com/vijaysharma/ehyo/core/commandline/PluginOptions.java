package com.vijaysharma.ehyo.core.commandline;

import java.util.HashSet;

public class PluginOptions {

	private final HashSet<String> plugins;
	private final String plugin;

	public PluginOptions(String plugin, HashSet<String> plugins) {
		this.plugin = plugin;
		this.plugins = plugins;
	}
}
