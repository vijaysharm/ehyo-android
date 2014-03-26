package com.vijaysharma.ehyo.core.commandline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.collect.Lists;
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
		for ( CommandLineAction action : actions ) {
			action.configure(parser);
		}
		
		try {
			OptionSet options = parser.parse( args );
			Action action = findAction(options, actions);
			action.run();
		} catch ( OptionException ex ) {
			printUsage(parser);
		}
	}
	
	private void printUsage(OptionParser parser) {
		try {
			parser.printHelpOn(System.err);
		} catch (IOException e) {
			// ignore
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
