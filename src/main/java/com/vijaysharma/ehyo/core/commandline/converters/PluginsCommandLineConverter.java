package com.vijaysharma.ehyo.core.commandline.converters;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.api.ArgumentOption;
import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;

public class PluginsCommandLineConverter implements CommandLineConverter<Set<String>> {
	private final ArgumentOption<String> plugins;
	
	public PluginsCommandLineConverter() {
		plugins = new ArgumentOptionBuilder<String>("plugins")
				.withRequiredArg(String.class)
				.defaultsTo("com.vijaysharma.ehyo")
				.build();
	}
	
	/**
	 * TODO: Need to support multiple plugins
	 */
	@Override
	public void configure(CommandLineParser parser) {
		parser.addOptions(plugins);
	}

	@Override
	public Set<String> read(ParsedSet options) {
		ImmutableSet.Builder<String> plugins = ImmutableSet.builder();
		plugins.add("com.vijaysharma.ehyo");
		plugins.add(options.value(this.plugins));
		
		return plugins.build();
	}
}
