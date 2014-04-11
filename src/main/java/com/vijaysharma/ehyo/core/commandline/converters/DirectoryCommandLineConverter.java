package com.vijaysharma.ehyo.core.commandline.converters;

import java.io.File;

import com.vijaysharma.ehyo.core.commandline.ArgumentOption;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

public class DirectoryCommandLineConverter implements CommandLineConverter<File>{
	private final ArgumentOption<File> directory;
	
	public DirectoryCommandLineConverter() {
		this(new ArgumentOptionBuilder<File>("directory")
				.withRequiredArg(File.class)
				.defaultsTo(new File("."))
				.build());
	}
	
	DirectoryCommandLineConverter( ArgumentOption<File> directory ) {
		this.directory = directory;
	}
	
	@Override
	public void configure(CommandLineParser parser) {
		parser.addOptions(directory);
	}

	@Override
	public File read(ParsedSet options) {
		return options.value(directory);
	}
}
