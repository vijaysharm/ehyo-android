package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.logging.Outputter;

public class PluginLoader {
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
		ImmutableMap.Builder<String, Plugin> plugins = ImmutableMap.builder();
		try {
			Set<Class<? extends Plugin>> subTypesOf = reflections.getSubTypesOf(Plugin.class);
			for ( Class<? extends Plugin> pluginClass : subTypesOf ) {
				Plugin instance = pluginClass.newInstance();
				plugins.put(instance.name().toLowerCase(), instance);
			}
		} catch( Exception ex ) {
			Outputter.debug.exception("Failed to load Plugins", ex);
		}
		return plugins.build();
	}

	public <T> Collection<T> transform(Function<Plugin, T> transformer) {
		return  Collections2.transform(loadPlugins().values(), transformer);
	}
}
