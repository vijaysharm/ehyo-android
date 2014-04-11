package com.vijaysharma.ehyo.core.actions;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

public interface CommandLineAction {
	void configure(CommandLineParser parser);
	/**
	 * @return null if the {@link ParsedSet} does not contain the expected
	 *         result.
	 */
	Action getAction(ParsedSet options);
}
