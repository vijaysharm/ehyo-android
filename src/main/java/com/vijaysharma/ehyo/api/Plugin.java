package com.vijaysharma.ehyo.api;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public interface Plugin {
	String name();
	void configure(OptionParser parser);
	void execute(OptionSet options);
}
