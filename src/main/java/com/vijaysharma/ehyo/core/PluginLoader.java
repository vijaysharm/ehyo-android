package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.logging.Output;

public class PluginLoader {
	private final ReflectionProvider reflections;

	public PluginLoader(Set<String> pluginNamespace) {
		this(new ReflectionProvider(pluginNamespace));
	}

	PluginLoader(ReflectionProvider reflections) {
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
			Set<Class<? extends Plugin>> subTypesOf = reflections.get().getSubTypesOf(Plugin.class);
			for ( Class<? extends Plugin> pluginClass : subTypesOf ) {
				Plugin instance = pluginClass.newInstance();
				plugins.put(instance.name().toLowerCase(), instance);
			}
		} catch( Exception ex ) {
			Output.out.exception("Failed to load Plugins", ex);
		}
		return plugins.build();
	}

	public <T> Collection<T> transform(Function<Plugin, T> transformer) {
		return  Collections2.transform(loadPlugins().values(), transformer);
	}
	
	private static class ReflectionProvider implements Supplier<Reflections> {
		private final Set<String> pluginNamespaces;
		public ReflectionProvider(Set<String> pluginNamespaces) {
			this.pluginNamespaces = pluginNamespaces;
		}

		@Override
		public Reflections get() {
			return new Reflections(pluginNamespaces);
		}
	}
}
