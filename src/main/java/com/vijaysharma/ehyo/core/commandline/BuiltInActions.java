package com.vijaysharma.ehyo.core.commandline;

import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

class BuiltInActions implements CommandLineAction {
	private final ArgumentOption<String> version = new ArgumentOptionBuilder<String>("version").build();
	
	@Override
	public void configure(CommandLineParser parser) {
		parser.addOptions(version);
	}
	
	@Override
	public Action getAction(ParsedSet options) {
		if (options.has( version )) {
			return new Action() {
				@Override
				public void run() {
					Outputter.out.println("VERSION INFORMATION");
				}
			};
		}

		return null;
	}
}
