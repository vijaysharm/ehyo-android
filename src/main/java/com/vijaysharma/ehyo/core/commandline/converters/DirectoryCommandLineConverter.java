package com.vijaysharma.ehyo.core.commandline.converters;

import java.io.File;

import com.vijaysharma.ehyo.core.ProjectRegistryLoader;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption;
import com.vijaysharma.ehyo.core.commandline.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser;
import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.utils.GentleMessageException;

public class DirectoryCommandLineConverter implements CommandLineConverter<ProjectRegistry>{
	private final ArgumentOption<File> directory;
	private final ProjectRegistryLoaderFactory factory;
	
	public DirectoryCommandLineConverter() {
		this(new ArgumentOptionBuilder<File>("directory")
				.withRequiredArg(File.class)
				.defaultsTo(new File("."))
				.build(),
			new ProjectRegistryLoaderFactory());
	}
	
	DirectoryCommandLineConverter(ArgumentOption<File> directory,
								  ProjectRegistryLoaderFactory factory) {
		this.directory = directory;
		this.factory = factory;
	}
	
	@Override
	public void configure(CommandLineParser parser) {
		parser.addOptions(directory);
	}

	@Override
	public ProjectRegistry read(ParsedSet options) {
		File root = options.value(directory);
		ProjectRegistryLoader loader = factory.create();

		ProjectRegistry registry = loader.load(root);
		if ( registry.isEmpty() ) 
			throw new GentleMessageException("No Android projects found in " + root.getAbsolutePath());
		
		return registry;
	}
	
	static class ProjectRegistryLoaderFactory {
		ProjectRegistryLoader create() {
			return new ProjectRegistryLoader();
		}
	}
}
