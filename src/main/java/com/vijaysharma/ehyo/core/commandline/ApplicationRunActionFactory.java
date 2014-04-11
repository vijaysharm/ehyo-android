package com.vijaysharma.ehyo.core.commandline;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.RunActionBuilder;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverter;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverter;

/**
 * Captures the system arguments (help, dry-run, plugins, plugin, directory,
 * etc...) before forwarding the commands on to the plugins
 */
class ApplicationRunActionFactory implements CommandLineAction {
	private final List<String> args;
	private final DirectoryCommandLineConverter directory;
	private final PluginsCommandLineConverter plugins;
	private final RunActionBuilderFactory factory;
	
	private OptionSpecBuilder help;
	private OptionSpecBuilder dryrun;

	public ApplicationRunActionFactory(List<String> args) {
		this(args, 
			new DirectoryCommandLineConverter(), 
			new PluginsCommandLineConverter(),
			new DefaultRunActionBuilderFactory());
	}

	ApplicationRunActionFactory(List<String> args,
								DirectoryCommandLineConverter directory,
								PluginsCommandLineConverter plugins,
								RunActionBuilderFactory factory) {
		this.args = args;
		this.directory = directory;
		this.plugins = plugins;
		this.factory = factory;
	}

	@Override
	public void configure(OptionParser parser) {
		this.help = parser.accepts("help");
		this.dryrun = parser.accepts("dry-run");
		this.directory.configure(parser);
		this.plugins.configure(parser);
	}

	@Override
	public Action getAction(OptionSet options) {
		RunActionBuilder run = factory.create(this.args);
		run.setShowHelp(options.has(help));
		run.setDryrun(options.has(dryrun));
		run.setDirectory(directory.read(options));
		run.setPluginOptions(plugins.read(options));
		
		return run.build();
	}
	
	interface RunActionBuilderFactory {
		RunActionBuilder create(List<String> args);
	}
	
	private static class DefaultRunActionBuilderFactory implements RunActionBuilderFactory {
		@Override
		public RunActionBuilder create(List<String> args) {
			return new RunActionBuilder(args);
		}
	}
}
