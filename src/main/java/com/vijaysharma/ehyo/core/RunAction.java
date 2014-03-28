package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.ManifestActionFactory;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunAction implements Action {
	private final String[] args;
	private final ProjectRegistryLoader projectLoader;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;
	private final PluginLoader pluginLoader;
	private final PluginActionMapper actionMapper;

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
		this.actionMapper = new PluginActionMapper();
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
		
		Service bundle = create(plugin.get());
		execute(plugin.get().name(), plugin.get().execute(options, bundle));
	}

	/**
	 * 1) Load the file structure(??)
	 * 2) Figure out what action was returned by the plugin
	 * 3) run the handler for that action against the project structure
	 * 4) Ask the user which file (if many) the handler should be run against
	 * 5) Show the diff to the user
	 * 6) Apply the diff and save the modified files
	 */
	private void execute(String pluginName, List<? extends PluginAction> actions) {
		if ( actions == null || actions.isEmpty() )
			return;
		
		ProjectRegistry registry = this.projectLoader.load();
		for ( PluginAction action : actions ) {
			Optional<ManifestActionHandler> handler = actionMapper.get(action);
			if ( handler.isPresent() ) { perform( handler.get(), registry ); }
			else { Outputter.err.println(pluginName + " provided a bad action type. Exiting."); return; }
		}
	}

	private void perform(ManifestActionHandler handler, ProjectRegistry registry) {
		handler.handle( registry );
	}

	private Service create(Plugin plugin) {
		Collection<Plugin> plugins = pluginLoader.transform(Functions.<Plugin>identity());
		return new Service(plugins, new DefaultManifestActionFactory());
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			Outputter.debug.exception("Failed to log usage", e);
		}
	}
}
