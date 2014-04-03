package com.vijaysharma.ehyo.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class RunAction implements Action {
	private final String[] args;
	private final ProjectRegistryLoader projectLoader;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;
	private final PluginLoader pluginLoader;
	private final PluginActionHandlerFactory factory;

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
		factory = new PluginActionHandlerFactory();
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
	private void execute(String pluginName, List<? extends PluginAction> actions) {
		if ( actions == null || actions.isEmpty() )
			return;
		
		ProjectRegistry registry = this.projectLoader.load();

		boolean needsManifest = false;
//		boolean needsBuild = false;
		for ( PluginAction action : actions ) {
			if ( action instanceof ManifestAction ) needsManifest = true;
		}
		
		if ( needsManifest ) {
			List<AndroidManifest> manifests = select(registry.getAllAndroidManifests());
			Map<AndroidManifest, Document> manifestLookup = transform(manifests);
			for ( PluginAction action : actions ) {
				Optional<PluginActionHandler<?>> handler = get(action);
				if ( handler.isPresent() ) { perform( handler.get(), manifestLookup ); }
				else { Outputter.err.println(pluginName + " provided an unknown action type. Exiting."); return; }
			}
			
			saveChangesToManifest(manifestLookup);
		}
	}

	private void saveChangesToManifest(Map<AndroidManifest, Document> manifestLookup) {
		for ( Map.Entry<AndroidManifest, Document> manifest : manifestLookup.entrySet() ) {
			try {
				if ( dryrun ) {
					show(manifest.getKey(), manifest.getValue());
				} else {
					save(manifest.getKey(), manifest.getValue());
				}
			} catch (Exception ex) {
				
			}
		}
	}

	private void show(AndroidManifest key, Document modified) throws IOException {
		List<String> baseline = toListOfStrings(key.asXmlDocument());
		List<String> changed = toListOfStrings(modified);
		Patch diff = DiffUtils.diff(baseline, changed);
		
		Outputter.out.println("Modifying " + key.getPath());
		for (Delta delta: diff.getDeltas()) {
			Outputter.out.println(delta);
		}		
	}
	
	private void save(AndroidManifest key, Document modified) {
		
	}

	private Map<AndroidManifest, Document> transform(List<AndroidManifest> manifests) {
		ImmutableMap.Builder<AndroidManifest, Document> mapping = ImmutableMap.builder();
		for ( AndroidManifest manifest : manifests ) {
			mapping.put(manifest, manifest.asXmlDocument());
		}

		return mapping.build();
	}

	private List<AndroidManifest> select(List<AndroidManifest> manifests) {
		FileSelector<AndroidManifest> selector = new FileSelector<AndroidManifest>(new Function<AndroidManifest, String>() {
			@Override
			public String apply(AndroidManifest manifest) {
				return manifest.getProject() + ":" + manifest.getSourceSet();
			}
		});
		
//		return selector.select(manifests);
		return Lists.newArrayList(manifests.get(0));
	}
	
	private void perform(PluginActionHandler<?> handler, Map<AndroidManifest, Document> manifestLookup) {
		if ( handler instanceof ManifestActionHandler ) {
			for ( Map.Entry<AndroidManifest, Document> manifest : manifestLookup.entrySet() ) {
				((ManifestActionHandler)handler).modify(manifest.getValue());
			}
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
	
	private List<String> toListOfStrings(Document doc) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		xmlOutput.output(doc, stream);

		return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()));
	}
}
