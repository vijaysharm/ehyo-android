package com.vijaysharma.ehyo.core.commandline;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;

class BuiltInActions implements CommandLineAction {
	private OptionSpecBuilder version;
	
	@Override
	public void configure(OptionParser parser) {
		this.version = parser.accepts( "version" );
	}
	
	@Override
	public Action getAction(OptionSet options) {
		if (options.has(this.version)) {
			return new Action() {
				@Override
				public void run() {
					// TODO: dump the version information
					System.out.println( "VERSION INFORMATION" );
				}
			};
		}
		
		return null;
	}
}
