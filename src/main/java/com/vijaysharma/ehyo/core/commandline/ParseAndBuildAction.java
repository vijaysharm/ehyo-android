package com.vijaysharma.ehyo.core.commandline;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;

class ParseAndBuildAction implements Action {
	private final List<String> args;
	private final CommandLineActionsFactory factory;
	private final TextOutput out;
	
	public ParseAndBuildAction(List<String> args) {
		this(args, new CommandLineActionsFactory(), Output.out);
	}

	ParseAndBuildAction(List<String> args, CommandLineActionsFactory factory, TextOutput out) {
		this.args = args;
		this.factory = factory;
		this.out = out;
	}

	/**
	 * Will check the command line for the terminate-able commands like version,
	 * else will allow for the application to continue parsing the commands
	 */
	@Override
	public void run() {
		List<CommandLineAction> actions = factory.create();

		CommandLineParser parser = new CommandLineParser(getUsage());
		for ( CommandLineAction action : actions ) {
			action.configure(parser);
		}
		
		try {
			ParsedSet options = parser.parse(args);
			Action action = findAction(options, actions);
			action.run();
		} catch ( GentleMessageException ex ) {
			out.println(ex.getMessage());
		} catch ( UnsupportedOperationException ex ) {
			throw ex;
		} catch ( Exception ex ) {
			out.exception("Execution exception " + Joiner.on(" ").join(this.args), ex);
		}
	}

	private Action findAction(ParsedSet options, List<CommandLineAction> actions) {
		for ( CommandLineAction command : actions ) {
			Action action = command.getAction(options);
			if ( action != null )
				return action;
		}
		
		throw new UnsupportedOperationException("No action factory for specified command-line arguments.");
	}
	
	private String getUsage() {
		StringBuilder usage = new StringBuilder();
		usage.append("usage: ehyo [-v | --version] [-n | --dry-run] [--directory <dir>]\n");
		usage.append("            <command> [<args>]\n\n");
		
		usage.append("Options:\n");
		usage.append("    -v, --version\n");
		usage.append("        To see the program version\n");
		usage.append("    -n, --dry-run\n");
		usage.append("        To see the changes that can be applied by\n");
		usage.append("        a command without it affecting your project\n");
		usage.append("    --directory <dir>\n");
		usage.append("        To set the path to the Android Gradle project.\n");
		usage.append("        Uses the current directory as default.\n\n");

		usage.append("The most commonly used ehyo commands are:\n");
		usage.append("    list\n");
		usage.append("    permissions\n");
		usage.append("    dependencies\n");
		usage.append("    templates\n");
		
		return usage.toString();
	}

	static class CommandLineActionsFactory {
		public List<CommandLineAction> create() {
			List<CommandLineAction> actions = Lists.newLinkedList();
			actions.add(new BuiltInActions());
			actions.add(new ApplicationRunActionFactory());
			
			return actions; 
		}
	}
}
