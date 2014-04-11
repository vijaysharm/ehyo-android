package com.vijaysharma.ehyo.core.commandline;

import java.util.List;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.RunActionBuilder;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverter;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverter;

/**
 * Captures the system arguments (help, dry-run, plugins, plugin, directory,
 * etc...) before forwarding the commands on to the plugins
 */
class ApplicationRunActionFactory implements CommandLineAction {
	private final DirectoryCommandLineConverter directory;
	private final PluginsCommandLineConverter plugins;
	private final RunActionBuilderFactory factory;
	private final ArgumentOption<String> help;
	private final ArgumentOption<String> dryrun;

	public ApplicationRunActionFactory() {
		this(new DirectoryCommandLineConverter(), 
			 new PluginsCommandLineConverter(),
			 new DefaultRunActionBuilderFactory());
	}

	ApplicationRunActionFactory(DirectoryCommandLineConverter directory,
								PluginsCommandLineConverter plugins,
								RunActionBuilderFactory factory) {
		this.directory = directory;
		this.plugins = plugins;
		this.factory = factory;
		
		this.help = new ArgumentOptionBuilder<String>("help").build();
		this.dryrun = new ArgumentOptionBuilder<String>("dry-run").build();
	}
	
	@Override
	public void configure(CommandLineParser parser) {
		parser.addOptions(help, dryrun);
	}
	
	@Override
	public Action getAction(ParsedSet options) {
		RunActionBuilder run = factory.create(options.getRemainingArgs());
		run.setShowHelp(options.has(help));
		run.setDryrun(options.has(dryrun));
//		run.setDirectory(directory.read(options));
//		run.setPluginOptions(plugins.read(options));
		
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
