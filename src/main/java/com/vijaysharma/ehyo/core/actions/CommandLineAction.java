package com.vijaysharma.ehyo.core.actions;

import com.vijaysharma.ehyo.core.Action;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public interface CommandLineAction {
	void configure(OptionParser parser);
	
	/**
	 * @return null if the {@link OptionSet} does not contain the expected
	 *         result.
	 */
	Action getAction(OptionSet options);
}
