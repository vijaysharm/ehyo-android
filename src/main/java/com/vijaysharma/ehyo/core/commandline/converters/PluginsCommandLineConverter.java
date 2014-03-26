package com.vijaysharma.ehyo.core.commandline.converters;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class PluginsCommandLineConverter implements CommandLineConverter<PluginOptions> {

	private ArgumentAcceptingOptionSpec<String> pluginUrls;
	private ArgumentAcceptingOptionSpec<String> plugin;

	/**
	 * TODO: Need to support multiple plugins
	 */
	@Override
	public void configure(OptionParser parser) {
		pluginUrls = parser.accepts( "plugins" )
			.withRequiredArg()
			.ofType( String.class )
			.defaultsTo( "com.vijaysharma.ehyo" );
		
		plugin = parser.accepts("plugin")
			.withRequiredArg()
			.ofType(String.class);
	}

	@Override
	public PluginOptions read(OptionSet options) {
		ImmutableSet.Builder<String> plugins = ImmutableSet.builder();
		plugins.add("com.vijaysharma.ehyo");
		plugins.add(options.valueOf(pluginUrls));
		
		return new PluginOptions( options.valueOf(this.plugin), plugins.build() );
	}
}
