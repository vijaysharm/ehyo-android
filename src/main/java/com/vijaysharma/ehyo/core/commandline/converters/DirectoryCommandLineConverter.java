package com.vijaysharma.ehyo.core.commandline.converters;

import java.io.File;

import com.vijaysharma.ehyo.api.ArgumentOption;
import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.ArgumentOption.ArgumentOptionBuilder;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.core.ProjectRegistryLoader;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

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
		if ( ! root.exists() )
			throw new GentleMessageException("Directory " + root.getAbsolutePath() + " doesn't seem to exist");
		
		ProjectRegistryLoader loader = factory.create();

		ProjectRegistry registry = loader.load(root);
		if ( registry.isEmpty() ) 
			throw new GentleMessageException("No Android projects found in " + root.getAbsolutePath() + 
											 "\nConsider passing --directory <dir> to an existing Android Gradle project");
		
		return registry;
	}
	
	static class ProjectRegistryLoaderFactory {
		ProjectRegistryLoader create() {
			return new ProjectRegistryLoader();
		}
	}
}
