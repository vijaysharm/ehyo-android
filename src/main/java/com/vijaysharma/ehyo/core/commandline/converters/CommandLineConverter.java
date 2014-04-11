package com.vijaysharma.ehyo.core.commandline.converters;

import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

public interface CommandLineConverter <T> {
	void configure(CommandLineParser parser);
	T read(ParsedSet options);
}
