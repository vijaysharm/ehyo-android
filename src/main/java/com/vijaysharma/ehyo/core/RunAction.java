package com.vijaysharma.ehyo.core;

import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.BinaryFileChangeManager.BinaryFileChangeManagerFactory;
import com.vijaysharma.ehyo.core.FileChangeManager.FileChangeManagerFactory;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.ResourceChangeManager.ResourceChangeManagerFactory;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class RunAction implements Action {
	private final List<String> args;
	private final ProjectRegistry registry;
	private final PluginLoader pluginLoader;
	private final ManifestChangeManagerFactory manifestChangeFactory;
	private final GradleBuildChangeManagerFactory buildChangeFactory;
	private final FileChangeManagerFactory fileChangeFactory;
	private final ServiceFactory serviceFactory;
	private final ObjectFactory<PluginActions> actionFactory;
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
			 new FileChangeManagerFactory(),
			 new ServiceFactory(),
			 new DefaultPluginActionFactory(),
			 help,
			 dryrun,
			 Output.out);
	}

	RunAction(List<String> args, 
			  PluginLoader loader,
			  ProjectRegistry registry, 
			  ManifestChangeManagerFactory manifestChangeFactory,
			  GradleBuildChangeManagerFactory buildChangeFactory,
			  FileChangeManagerFactory fileChangeFactory,
			  ServiceFactory serviceFactory,
			  ObjectFactory<PluginActions> actionFactory,
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
		this.fileChangeFactory = fileChangeFactory;
		this.serviceFactory = serviceFactory;
		this.actionFactory = actionFactory;
		this.out = out;
	}

	@Override
	public void run() {
		Plugin plugin = find(args);
		
		if ( ! args.isEmpty() )
			out.println("Executing plugin: " + plugin.getClass().getCanonicalName() + " with args: " + Joiner.on(" ").join(args));

		if ( help ) {
			out.println("TODO: Print usage for: " + plugin.name());
		} else {
			PluginActions actions = actionFactory.create();
			Service service = serviceFactory.create(pluginLoader, registry, actions);
			plugin.execute(args, service);
			execute(actions, registry);
		}
	}

	private void execute(PluginActions actions, ProjectRegistry registry) {
		if ( actions.hasManifestChanges() ) {
			ManifestChangeManager changes = manifestChangeFactory.create();
			changes.apply(actions);
			changes.commit(dryrun);
		}
		
		if ( actions.hasBuildChanges() ) {
			GradleBuildChangeManager changes = buildChangeFactory.create();
			changes.apply(actions);
			changes.commit(dryrun);
		}
		
		if ( actions.hasFileChanges() ) {
			FileChangeManager changes = fileChangeFactory.create();
			changes.apply(actions);
			changes.commit(dryrun);
		}
		
		// TODO: Move factories into constructor
		if ( actions.hasResourceChanges() ) {
			ResourceChangeManager changes = new ResourceChangeManagerFactory().create();
			changes.apply(actions);
			changes.commit(dryrun);
		}
		
		if ( actions.hasBinaryFileChanges() ) {
			BinaryFileChangeManager changes = new BinaryFileChangeManagerFactory().create();
			changes.apply(actions);
			changes.commit(dryrun);
		}
	}
	
	private Plugin find(List<String> args) {
		if ( args == null || args.isEmpty() ) {
			throw new GentleMessageException("You haven't defined a command to run.\n" +
											 "try running 'ehyo list' to see a list of available commands\n");
		}
		
		String pluginName = args.remove(0);
		if ( pluginName == null ) {
			throw new RuntimeException("No command found");
		}
		
		Optional<Plugin> p = pluginLoader.findPlugin(pluginName);
		if ( ! p.isPresent() ) {
			throw new GentleMessageException("Command [" + pluginName + "] is not supported by ehyo.\n" +
											 "try running 'ehyo list' to see a list of available commands\n");
		}
		
		return p.get();
	}

	private static class DefaultPluginActionFactory implements ObjectFactory<PluginActions> {
		@Override
		public PluginActions create() {
			return new PluginActions();
		}
	}
}
