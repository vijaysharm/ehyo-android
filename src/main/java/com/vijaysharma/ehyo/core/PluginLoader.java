package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.api.Plugin;

public class PluginLoader {
	private static final Logger l = LoggerFactory.getLogger(PluginLoader.class);
	private final Reflections reflections;

	public PluginLoader(Set<String> pluginNamespace) {
		this(new Reflections(pluginNamespace));
	}

	PluginLoader(Reflections reflections) {
		this.reflections = reflections;
	}

	public Optional<Plugin> findPlugin(String name) {
		Map<String, Plugin> plugins = loadPlugins();
		return Optional.fromNullable(plugins.get(name.toLowerCase()));
	}
	
	/**
	 * We load all the plugins only to use one of them.
	 */
	private Map<String, Plugin> loadPlugins() {
		Map<String, Plugin> plugins = Maps.newHashMap();
		try {
			Set<Class<? extends Plugin>> subTypesOf = reflections.getSubTypesOf(Plugin.class);
			for ( Class<? extends Plugin> pluginClass : subTypesOf ) {
				Plugin instance = pluginClass.newInstance();
				plugins.put(instance.name().toLowerCase(), instance);
			}
		} catch( Exception ex ) {
			l.debug("Failed to load Plugins", ex);
		}
		return plugins;
	}
}
