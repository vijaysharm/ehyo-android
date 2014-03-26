package com.vijaysharma.ehyo.core.commandline;

import static com.google.common.base.Joiner.on;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;

class ParseAndBuildAction implements Action {
	private final String[] args;
	private final CommandLineActionsFactory factory;
	
	public ParseAndBuildAction(String[] args) {
		this(args, new DefaultCommandLineActionsFactory());
	}

	ParseAndBuildAction(String[] args, CommandLineActionsFactory factory) {
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

		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		for ( CommandLineAction action : actions ) {
			action.configure(parser);
		}
		
		try {
			OptionSet options = parser.parse( args );
			Action action = findAction(options, actions);
			action.run();
		} catch ( UnsupportedOperationException ex ) {
			throw ex;
		} catch ( Exception ex ) {
			Outputter.debug.exception("Execution exception " + on(" ").join(this.args), ex);
			printUsage(ex.getMessage(), parser);
		}
	}
	
	private void printUsage(String message, OptionParser parser) {
		try {
			System.err.println(message);
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			Outputter.debug.exception("Failed to print usage", e);
		}		
	}

	private Action findAction(OptionSet options, List<CommandLineAction> actions) {
		for ( CommandLineAction command : actions ) {
			Action action = command.getAction(options);
			if ( action != null )
				return action;
		}
		
		throw new UnsupportedOperationException("No action factory for specified command-line arguments.");
	}
	
	static interface CommandLineActionsFactory {
		List<CommandLineAction> create(String[] args);
	}
	
	private static class DefaultCommandLineActionsFactory implements CommandLineActionsFactory {
		@Override
		public List<CommandLineAction> create(String[] args) {
			ArrayList<CommandLineAction> actions = Lists.newArrayList();
			actions.add(new BuiltInActions());
			actions.add(new ApplicationRunActionFactory(args));
			
			return actions; 
		}
	}
}
