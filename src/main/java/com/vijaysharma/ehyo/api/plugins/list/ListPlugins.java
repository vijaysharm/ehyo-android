package com.vijaysharma.ehyo.api.plugins.list;

import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Outputter;

public class ListPlugins implements Plugin {
	
	@Override
	public String name() {
		return "list";
	}
	
	@Override
	public List<PluginAction> execute(List<String> args, Service service) {
		if ( args.contains("--about") ) {
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
