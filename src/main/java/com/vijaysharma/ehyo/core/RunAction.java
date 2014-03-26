package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunAction implements Action {
	private static final Logger l = LoggerFactory.getLogger(RunAction.class);
	
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
		this(args, new ProjectRegistryLoader(root), pluginOptions, dryrun, help, new PluginLoader(pluginOptions.getPlugins()));
	}

	RunAction(String[] args, 
			  ProjectRegistryLoader projectLoader, 
			  PluginOptions pluginOptions,
			  boolean dryrun, 
			  boolean help,
			  PluginLoader loader) {
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
		plugin.get().execute(options);
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			l.debug("Failed to log usage", e);
		}
	}
}
