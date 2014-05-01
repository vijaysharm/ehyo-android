package com.vijaysharma.ehyo.core.actions;

import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.Action;

public interface CommandLineAction {
	void configure(CommandLineParser parser);
	/**
	 * @return null if the {@link ParsedSet} does not contain the expected
	 *         result.
	 */
	Action getAction(ParsedSet options);
}
