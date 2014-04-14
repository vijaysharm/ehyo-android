package com.vijaysharma.ehyo.api.plugins.list;

import java.util.List;

import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.logging.Output;

public class ListPlugins implements Plugin {
	
	@Override
	public String name() {
		return "list";
	}
	
	@Override
	public void execute(List<String> args, Service service) {
		if ( args.contains("--about") ) {
			Output.out.println("TODO: Print about information");
			return;
		}
		
		Output.out.println( "Installed plugins" );
		Output.out.println( "-----------------" );
		for ( Plugin plugin : service.getPlugins() ) {
			Output.out.println( "+ " + plugin.name() );
		}
	}
}
