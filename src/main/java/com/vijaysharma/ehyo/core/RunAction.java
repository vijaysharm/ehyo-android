package com.vijaysharma.ehyo.core;

import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunAction implements Action {
	private final List<String> args;
	private final ProjectRegistry registry;
	private final PluginLoader pluginLoader;
	private final ManifestChangeManagerFactory manifestChangeFactory;
	private final GradleBuildChangeManagerFactory buildChangeFactory;
	private final ServiceFactory serviceFactory;
	private final boolean dryrun;
	private final boolean help;	
	private final TextOutput out;

	public RunAction(List<String> args, 
					 ProjectRegistry registry, 
					 Set<String> pluginNamespaces,
					 boolean dryrun, 
					 boolean help) {
		this(args, 
			 new PluginLoader(pluginNamespaces),
			 registry,
			 new ManifestChangeManagerFactory(),
			 new GradleBuildChangeManagerFactory(),
			 new ServiceFactory(),
			 help,
			 dryrun,
			 Output.out);
	}

	RunAction(List<String> args, 
			  PluginLoader loader,
			  ProjectRegistry registry, 
			  ManifestChangeManagerFactory manifestChangeFactory,
			  GradleBuildChangeManagerFactory buildChangeFactory,
			  ServiceFactory serviceFactory,
			  boolean help,
			  boolean dryrun,
			  TextOutput out) {
		this.args = args;
		this.registry = registry;
		this.dryrun = dryrun;
		this.help = help;
		this.pluginLoader = loader;
		this.manifestChangeFactory = manifestChangeFactory; 
		this.buildChangeFactory = buildChangeFactory;
		this.serviceFactory = serviceFactory;
		this.out = out;
	}

	@Override
	public void run() {
		Plugin plugin = find(args);
		out.println("Executing plugin: " + plugin.getClass().getCanonicalName() + " with args: " + Joiner.on(" ").join(args));

		if ( help ) {
			out.println("TODO: Print usage for: " + plugin.name());
		} else {
			PluginActions actions = new PluginActions();
			Service service = serviceFactory.create(pluginLoader, registry, actions);
			plugin.execute(args, service);
			execute(plugin.name(), actions, registry);
		}
	}

	/**
	 * 1) Load the file structure(??)
	 * 2) Figure out what action was returned by the plugin
	 * 3) run the handler for that action against the project structure
	 * 4) Ask the user which file (if many) the handler should be run against
	 * 5) Show the diff to the user
	 * 6) Apply the diff and save the modified files
	 * 
	 * TODO: Collapse ManifestActions and BuildActions into a single object
	 */
	private void execute(String pluginName, PluginActions actions, ProjectRegistry registry) {
		if ( actions.hasManifestChanges() ) {
			ManifestChangeManager changes = manifestChangeFactory.create(registry);
			changes.apply(actions);
			changes.commit(dryrun);
		}
		
		if ( actions.hasBuildChanges() ) {
			GradleBuildChangeManager changes = buildChangeFactory.create(registry);
			changes.apply(actions);
			changes.commit(dryrun);
		}
	}
	
	private Plugin find(List<String> args) {
		if ( args == null || args.isEmpty() ) {
			throw new RuntimeException("No plugin defined");
		}
		
		String pluginName = args.remove(0);
		if ( pluginName == null ) {
			throw new RuntimeException("No plugin given");
		}
		
		Optional<Plugin> p = pluginLoader.findPlugin(pluginName);
		if ( ! p.isPresent() ) {
			throw new RuntimeException("Plugin [" + pluginName + "] was not found.");
		}
		
		return p.get();
	}

//	private void printUsage(OptionParser parser) {
//		try {
//			parser.printHelpOn(System.err);
//		} catch (IOException e) {
//			Outputter.debug.exception("Failed to log usage", e);
//		}
//	}
}
