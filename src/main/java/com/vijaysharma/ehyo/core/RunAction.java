package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.base.Function;
import com.google.common.base.Functions;
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
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.core.GradleBuildChangeManager.GradleBuildChangeManagerFactory;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.ManifestChangeManager.ManifestChangeManagerFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildActionFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultManifestActionFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultOptionSelectorFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectBuild;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectManifest;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;
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
	private final OptionSelector<AndroidManifest> manifestSelector;
	private final ManifestChangeManagerFactory manifestChangeFactory;
	private final GradleBuildChangeManagerFactory buildChangeFactory;

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
			 new OptionSelector<AndroidManifest>("Which of the following would you like to modify", MANIFEST_RENDERER),
			 new ManifestChangeManagerFactory(),
			 new GradleBuildChangeManagerFactory(),
			 help,
			 dryrun);
	}

	RunAction(String[] args, 
			  PluginOptions pluginOptions, 
			  PluginLoader loader,
			  ProjectRegistryLoader projectLoader, 
			  PluginActionHandlerFactory factory,
			  OptionSelector<AndroidManifest> manifestSelector,
			  ManifestChangeManagerFactory manifestChangeFactory,
			  GradleBuildChangeManagerFactory buildChangeFactory,
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
		this.buildChangeFactory = buildChangeFactory;
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
		
		ProjectRegistry registry = this.projectLoader.load();
		Service service = create(plugin, registry);
		execute(plugin.name(), plugin.execute(options, service), registry);
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
			List<AndroidManifest> manifests = manifestSelector.select(registry.getAllAndroidManifests());
			ManifestChangeManager changes = manifestChangeFactory.create(manifests);
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

	private Service create(Plugin plugin, ProjectRegistry registry) {
		Collection<Plugin> plugins = pluginLoader.transform(Functions.<Plugin>identity());
		
		final ImmutableList.Builder<BuildConfiguration> configuration = ImmutableList.builder();
		
		List<ProjectBuild> builds = registry.getAllGradleBuilds(new Function<GradleBuild, ProjectBuild>() {
			@Override
			public ProjectBuild apply(GradleBuild build) {
				configuration.addAll(buildConfiguration(build));
				return new DefaultProjectBuild(build);
			}

			private List<BuildConfiguration> buildConfiguration(GradleBuild build) {
				List<BuildConfiguration> configuration = Lists.newArrayList();
				List<BuildType> buildTypes = build.getBuildTypes();
				List<Flavor> flavors = build.getFlavors();

				for ( BuildType buildType : buildTypes ) {
					configuration.add(new DefaultBuildConfiguration( buildType, null, build));
					for ( Flavor flavor : flavors ) {
						configuration.add(new DefaultBuildConfiguration( buildType, flavor, build ));
					}
				}
				
				return configuration;
			}
		});
		
		List<ProjectManifest> manifests = registry.getAllAndroidManifests(new Function<AndroidManifest, ProjectManifest>() {
			@Override
			public ProjectManifest apply(AndroidManifest manifest) {
				return new DefaultProjectManifest(manifest);
			}
		});
		
		return new Service(plugins, 
						   manifests,
						   builds,
						   configuration.build(),
						   new DefaultManifestActionFactory(), 
						   new DefaultBuildActionFactory(),
						   new DefaultOptionSelectorFactory());
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			Outputter.debug.exception("Failed to log usage", e);
		}
	}
}
