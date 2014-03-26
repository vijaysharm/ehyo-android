package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginBundle;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunAction implements Action {
	private final String[] args;
	private final ProjectRegistryLoader projectLoader;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;
	private final PluginLoader pluginLoader;

	public RunAction(String[] args, 
					 File root, 
					 PluginOptions pluginOptions,
					 boolean dryrun, 
					 boolean help) {
		this(args, pluginOptions, new PluginLoader(pluginOptions.getPlugins()), new ProjectRegistryLoader(root), help, dryrun);
	}

	RunAction(String[] args, 
			  PluginOptions pluginOptions, 
			  PluginLoader loader,
			  ProjectRegistryLoader projectLoader, 
			  boolean help,
			  boolean dryrun) {
		this.args = args;
		this.projectLoader = projectLoader;
		this.pluginOptions = pluginOptions;
		this.dryrun = dryrun;
		this.help = help;
		this.pluginLoader = loader;
	}

	@Override
	public void run() {
		Optional<Plugin> plugin = pluginLoader.findPlugin(this.pluginOptions.getPlugin());
		
		if ( ! plugin.isPresent() ) {
			throw new RuntimeException("Plugin [" + this.pluginOptions.getPlugin() + "] was not found.");
		}
			
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		plugin.get().configure(parser);
		if ( help ) {
			printUsage(parser);
			return;
		}
		
		OptionSet options = parser.parse(this.args);
		
		PluginBundle bundle = buildBundle(plugin.get());
		plugin.get().execute(options, bundle);
	}

	private PluginBundle buildBundle(Plugin plugin) {
		Collection<Plugin> plugins = pluginLoader.transform(Functions.<Plugin>identity());
		return new PluginBundle(plugins);
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			Outputter.debug.exception("Failed to log usage", e);
		}
	}
}
