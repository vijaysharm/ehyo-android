package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunAction implements Action {
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	private final String[] args;
	private final ProjectRegistryLoader projectLoader;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;
	private final PluginLoader pluginLoader;
	private final PluginActionHandlerFactory factory;
	private final FileSelector<AndroidManifest> manifestSelector;
	private final ManifestChangeManagerFactory manifestChangeFactory;

	public RunAction(String[] args, 
					 File root, 
					 PluginOptions pluginOptions,
					 boolean dryrun, 
					 boolean help) {
		this(args, 
			 pluginOptions, 
			 new PluginLoader(pluginOptions.getPluginNamespaces()),
			 new ProjectRegistryLoader(root),
			 new PluginActionHandlerFactory(),
			 new FileSelector<AndroidManifest>(MANIFEST_RENDERER),
			 new ManifestChangeManagerFactory(),
			 help,
			 dryrun);
	}

	RunAction(String[] args, 
			  PluginOptions pluginOptions, 
			  PluginLoader loader,
			  ProjectRegistryLoader projectLoader, 
			  PluginActionHandlerFactory factory,
			  FileSelector<AndroidManifest> manifestSelector,
			  ManifestChangeManagerFactory manifestChangeFactory,
			  boolean help,
			  boolean dryrun) {
		this.args = args;
		this.projectLoader = projectLoader;
		this.pluginOptions = pluginOptions;
		this.manifestSelector = manifestSelector;
		this.dryrun = dryrun;
		this.help = help;
		this.pluginLoader = loader;
		this.factory = factory;
		this.manifestChangeFactory = manifestChangeFactory; 
	}

	@Override
	public void run() {
		Optional<Plugin> p = pluginLoader.findPlugin(this.pluginOptions.getPlugin());
		
		if ( ! p.isPresent() ) {
			throw new RuntimeException("Plugin [" + this.pluginOptions.getPlugin() + "] was not found.");
		}
		
		Plugin plugin = p.get();
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		plugin.configure(parser);
		if ( help ) {
			printUsage(parser);
			return;
		}
		
		OptionSet options = parser.parse(this.args);
		
		Service service = create(plugin);
		execute(plugin.name(), plugin.execute(options, service));
	}

	/**
	 * 1) Load the file structure(??)
	 * 2) Figure out what action was returned by the plugin
	 * 3) run the handler for that action against the project structure
	 * 4) Ask the user which file (if many) the handler should be run against
	 * 5) Show the diff to the user
	 * 6) Apply the diff and save the modified files
	 */
	private void execute(String pluginName, List<PluginAction> actions) {
		if ( actions == null || actions.isEmpty() )
			return;
		
		ProjectRegistry registry = this.projectLoader.load();

		boolean needsManifest = false;
//		boolean needsBuild = false;
		for ( PluginAction action : actions ) {
			if ( action instanceof ManifestAction ) needsManifest = true;
		}
		
		if ( needsManifest ) {
			List<AndroidManifest> manifests = manifestSelector.select(registry.getAllAndroidManifests());
			ManifestChangeManager changes = manifestChangeFactory.create(manifests);
			for ( PluginAction action : actions ) {
				Optional<PluginActionHandler<?>> handler = get(action);
				if ( handler.isPresent() ) { changes.apply(handler.get()); }
				else { Outputter.err.println(pluginName + " provided an unknown action type."); }
			}
			
			changes.commit(dryrun);
		}
	}

	private Service create(Plugin plugin) {
		Collection<Plugin> plugins = pluginLoader.transform(Functions.<Plugin>identity());
		return new Service(plugins, new DefaultManifestActionFactory());
	}
	
	private Optional<PluginActionHandler<?>> get(PluginAction action) {
		PluginActionHandler<?> handler = factory.create(action);
		if (handler == null)
			return Optional.absent();
		
		return Optional.<PluginActionHandler<?>>of(handler);
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			Outputter.debug.exception("Failed to log usage", e);
		}
	}
}
