package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunAction implements Action {
	private final String[] args;
	private final File root;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;

	public RunAction(String[] args, 
					 File root, 
					 PluginOptions pluginOptions,
					 boolean dryrun, 
					 boolean help) {
		this.args = args;
		this.root = root;
		this.pluginOptions = pluginOptions;
		this.dryrun = dryrun;
		this.help = help;
	}

	@Override
	public void run() {
		try {
			Reflections reflections = new Reflections("com.vijaysharma.ehyo");
			Map<String, Plugin> plugins = loadPlugins(reflections);
			Plugin plugin = plugins.get(this.pluginOptions.getPlugin().get().toLowerCase());
			if ( plugin == null ) {
				// TODO: Show usage
				return;
			}
		}
		catch( Exception ex ) {
			
		}
	}
	
	private static Map<String, Plugin> loadPlugins(Reflections reflections) throws Exception {
		Map<String, Plugin> plugins = Maps.newHashMap();
		Set<Class<? extends Plugin>> subTypesOf = reflections.getSubTypesOf(Plugin.class);
		for ( Class<? extends Plugin> pluginClass : subTypesOf ) {
			Plugin instance = pluginClass.newInstance();
			plugins.put(instance.name().toLowerCase(), instance);
		}

		return plugins;
	}
}
