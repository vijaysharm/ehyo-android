package com.vijaysharma.ehyo.api.plugins;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.vijaysharma.ehyo.api.Plugin;

public class ListPlugins implements Plugin {
	
	private OptionSpecBuilder about;

	@Override
	public String name() {
		return ListPlugins.class.getSimpleName();
	}

	@Override
	public void configure(OptionParser parser) {
		about = parser.accepts("about");
	}

	@Override
	public void execute(OptionSet options) {
		if ( options.has(about) ) {
			System.err.println("This is about me.");
		}
	}
}
