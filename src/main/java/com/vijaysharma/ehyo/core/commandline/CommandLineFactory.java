package com.vijaysharma.ehyo.core.commandline;

import com.vijaysharma.ehyo.core.Action;

public class CommandLineFactory {
	private ActionFactory actionFactory;

	public CommandLineFactory() {
		this( new DefaultCommandLineActionFactory() );
	}
	
	CommandLineFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}
	public Action configure(String[] args) {
		return actionFactory.create(args);
	}
	
	static interface ActionFactory {
		Action create(String[] args);
	}
	
	private static class DefaultCommandLineActionFactory implements ActionFactory {
		@Override
		public Action create(String[] args) {
			return new ParseAndBuildAction( args );
		}
	}
}
