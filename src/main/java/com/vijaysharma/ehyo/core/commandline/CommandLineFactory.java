package com.vijaysharma.ehyo.core.commandline;

import java.util.List;

import com.vijaysharma.ehyo.core.Action;

public class CommandLineFactory {
	private ActionFactory actionFactory;

	public CommandLineFactory() {
		this( new DefaultCommandLineActionFactory() );
	}
	
	CommandLineFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}
	public Action configure(List<String> args) {
		return actionFactory.create(args);
	}
	
	static interface ActionFactory {
		Action create(List<String> args);
	}
	
	private static class DefaultCommandLineActionFactory implements ActionFactory {
		@Override
		public Action create(List<String> args) {
			return new ParseAndBuildAction( args );
		}
	}
}
