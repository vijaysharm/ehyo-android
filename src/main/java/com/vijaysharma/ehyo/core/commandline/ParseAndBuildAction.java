package com.vijaysharma.ehyo.core.commandline;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

class ParseAndBuildAction implements Action {
	private final List<String> args;
	private final CommandLineActionsFactory factory;
	
	public ParseAndBuildAction(List<String> args) {
		this(args, new CommandLineActionsFactory());
	}

	ParseAndBuildAction(List<String> args, CommandLineActionsFactory factory) {
		this.args = args;
		this.factory = factory;
	}

	/**
	 * Will check the command line for the terminate-able commands like version,
	 * else will allow for the application to continue parsing the commands
	 */
	@Override
	public void run() {
		List<CommandLineAction> actions = factory.create(args);

		CommandLineParser parser = new CommandLineParser();
		for ( CommandLineAction action : actions ) {
			action.configure(parser);
		}
		
		try {
			ParsedSet options = parser.parse(args);
			Action action = findAction(options, actions);
			action.run();
		} catch ( UnsupportedOperationException ex ) {
			throw ex;
		} catch ( Exception ex ) {
			Outputter.debug.exception("Execution exception " + Joiner.on(" ").join(this.args), ex);
//			printUsage(ex.getMessage(), parser);
		}
	}
	
//	private void printUsage(String message, OptionParser parser) {
//		try {
//			System.err.println(message);
//			parser.printHelpOn(System.err);
//		} catch (IOException e) {
//			Outputter.debug.exception("Failed to print usage", e);
//		}		
//	}

	private Action findAction(ParsedSet options, List<CommandLineAction> actions) {
		for ( CommandLineAction command : actions ) {
			Action action = command.getAction(options);
			if ( action != null )
				return action;
		}
		
		throw new UnsupportedOperationException("No action factory for specified command-line arguments.");
	}
	
	static class CommandLineActionsFactory {
		public List<CommandLineAction> create(List<String> args) {
			ArrayList<CommandLineAction> actions = Lists.newArrayList();
			actions.add(new BuiltInActions());
			actions.add(new ApplicationRunActionFactory());
			
			return actions; 
		}
	}
}
