package com.vijaysharma.ehyo.core.commandline.converters;

import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;

public interface CommandLineConverter <T> {
	void configure(CommandLineParser parser);
	T read(ParsedSet options);
}
