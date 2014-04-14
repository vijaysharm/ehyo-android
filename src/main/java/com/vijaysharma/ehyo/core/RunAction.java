package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultOptionSelectorFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectBuild;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunAction implements Action {
	private final List<String> args;
	private final ProjectRegistry registry;
	private final PluginLoader pluginLoader;
	private final PluginActionHandlerFactory factory;
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
			 new PluginActionHandlerFactory(),
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
			  PluginActionHandlerFactory factory,
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
		this.factory = factory;
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
			Service service = serviceFactory.create(pluginLoader, registry);
			plugin.execute(args, service);
			execute(plugin.name(), gather(service), registry);
		}
	}

	/**
	 * 1) Load the file structure(??)
	 * 2) Figure out what action was returned by the plugin
	 * 3) run the handler for that action against the project structure
	 * 4) Ask the user which file (if many) the handler should be run against
	 * 5) Show the diff to the user
	 * 6) Apply the diff and save the modified files
	 */
	private void execute(String pluginName, List<PluginAction> actions, ProjectRegistry registry) {
		if ( actions == null || actions.isEmpty() )
			return;

		Set<InternalManifestAction> manifestActions = Sets.newHashSet();
		Set<InternalBuildAction> buildActions = Sets.newHashSet();
		for ( PluginAction action : actions ) {
			if ( action instanceof InternalManifestAction ) manifestActions.add((InternalManifestAction)action);
			if ( action instanceof InternalBuildAction ) buildActions.add((InternalBuildAction)action);
		}
		
		if ( ! manifestActions.isEmpty() ) {
			ManifestChangeManager changes = manifestChangeFactory.create(registry, manifestActions);
			for ( InternalManifestAction action : manifestActions ) {
				ManifestActionHandler handler = factory.createManifestActionHandler(action);
				changes.apply(handler);
			}
			
			changes.commit(dryrun);
		}
		
		if ( ! buildActions.isEmpty() ) {
			GradleBuildChangeManager changes = buildChangeFactory.create(registry, buildActions);
			for ( InternalBuildAction action : buildActions ) {
				BuildActionHandler handler = factory.createBuildActionHandler(action);
				changes.apply(handler);
			}
			
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

	private List<PluginAction> gather(Service service) {
		ImmutableList.Builder<PluginAction> actions = ImmutableList.builder();
		List<BuildConfiguration> configurations = service.getConfigurations();
		for ( BuildConfiguration config : configurations ) {
			DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
			if ( configuration.getBuildAction().hasChanges() )
				actions.add(configuration.getBuildAction());
		}
		
		List<ProjectManifest> manifests = service.getManifests();
		for ( ProjectManifest man : manifests ) {
			DefaultProjectManifest manifest = (DefaultProjectManifest) man;
			if ( manifest.getManifestActions().hasChanges() ) {
				actions.add(manifest.getManifestActions());
			}
		}
		
		return actions.build();
	}

//	private void printUsage(OptionParser parser) {
//		try {
//			parser.printHelpOn(System.err);
//		} catch (IOException e) {
//			Outputter.debug.exception("Failed to log usage", e);
//		}
//	}
}
