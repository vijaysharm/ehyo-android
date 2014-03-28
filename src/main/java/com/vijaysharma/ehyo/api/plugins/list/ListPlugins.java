package com.vijaysharma.ehyo.api.plugins.list;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;

public class ListPlugins implements Plugin {
	
	private OptionSpecBuilder about;

	@Override
	public String name() {
		return "list";
	}

	@Override
	public void configure(OptionParser parser) {
		about = parser.accepts("about");
	}

	@Override
	public List<? extends PluginAction> execute(OptionSet options, Service service) {
		if ( options.has(about) ) {
			Outputter.out.println("TODO: Print about information");
			return Lists.newArrayList();
		}
		
		Outputter.out.println( "Installed plugins" );
		Outputter.out.println( "-----------------" );
		for ( Plugin plugin : service.getPlugins() ) {
			Outputter.out.println( "+ " + plugin.name() );
		}
		return Lists.newArrayList();
	}
}
