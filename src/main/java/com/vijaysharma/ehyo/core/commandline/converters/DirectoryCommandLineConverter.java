package com.vijaysharma.ehyo.core.commandline.converters;

import java.io.File;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class DirectoryCommandLineConverter implements CommandLineConverter<File>{
	private ArgumentAcceptingOptionSpec<File> directory;
	
	@Override
	public void configure(OptionParser parser) {
		directory = parser.accepts( "directory" )
			.withRequiredArg()
			.ofType( File.class )
			.defaultsTo( new File(".") );		
	}

	@Override
	public File read(OptionSet options) {
		return directory.value(options);
	}
}
