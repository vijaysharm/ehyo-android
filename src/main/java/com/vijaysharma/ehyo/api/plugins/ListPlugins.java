package com.vijaysharma.ehyo.api.plugins;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginBundle;
import com.vijaysharma.ehyo.api.logging.Outputter;

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
	public void execute(OptionSet options, PluginBundle bundle) {
		if ( options.has(about) ) {
			Outputter.out.println("TODO: Print about information");
			return;
		}
		
		Outputter.out.println( "Installed plugins" );
		Outputter.out.println( "-----------------" );
		for ( Plugin plugin : bundle.getPlugins() ) {
			Outputter.out.println( "+ " + plugin.name() );
		}
	}
}
