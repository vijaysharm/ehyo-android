package com.vijaysharma.ehyo.core.commandline;

import java.util.List;

import com.vijaysharma.ehyo.core.Action;

public class CommandLineFactory {
	private ActionFactory actionFactory;

	public CommandLineFactory() {
		this( new ActionFactory() );
	}
	
	CommandLineFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}
	public Action configure(List<String> args) {
		return actionFactory.create(args);
	}
	
	static class ActionFactory {
		public Action create(List<String> args) {
			return new ParseAndBuildAction( args );
		}
	}
}
