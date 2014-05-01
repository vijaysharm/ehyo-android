package com.vijaysharma.ehyo.core.commandline;

import com.vijaysharma.ehyo.api.ArgumentOption;
import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;

class BuiltInActions implements CommandLineAction {
	private final ArgumentOption<String> version = new ArgumentOptionBuilder<String>("v", "version").build();
	private final TextOutput out;
	
	public BuiltInActions() {
		this(Output.out);
	}
	
	BuiltInActions(TextOutput out) {
		this.out = out;
	}

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
					out.println("VERSION INFORMATION");
				}
			};
		}

		return null;
	}
}
