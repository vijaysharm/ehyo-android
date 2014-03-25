package com.vijaysharma.ehyo.core.commandline.converters;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public interface CommandLineConverter <T> {
	void configure(OptionParser parser);
	T read(OptionSet options);
}
