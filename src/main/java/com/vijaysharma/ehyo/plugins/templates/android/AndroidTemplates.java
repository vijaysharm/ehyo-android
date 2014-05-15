package com.vijaysharma.ehyo.plugins.templates.android;

import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.api.TemplateFileParameter;
import com.vijaysharma.ehyo.api.TemplateInfo;
import com.vijaysharma.ehyo.api.TemplateProperty;
import com.vijaysharma.ehyo.api.UsageException;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.api.utils.Questioner;
import com.vijaysharma.ehyo.api.utils.Questioner.AnswerFactory;
import com.vijaysharma.ehyo.api.utils.Questioner.Question;
import com.vijaysharma.ehyo.plugins.templates.android.TemplateRegistry.TemplateItem;

public class AndroidTemplates implements Plugin {
	private final TemplateRegistry registry;
	private final TextOutput out;
	
	public AndroidTemplates() {
		this(new TemplateRegistry(), Output.out);
	}
	
	AndroidTemplates(TemplateRegistry templateRegistry, TextOutput out) {
		this.registry = templateRegistry;
		this.out = out;
	}

	@Override
	public String name() {
		return "templates";
	}

	@Override
	public String usage() {
		StringBuilder usage = new StringBuilder();
//		usage.append("usage: ehyo templates [-l | --list]\n");
//		usage.append("                      [-i | --info <template name>]\n");
//		usage.append("                      [-a | --apply <template name>]\n\n");
//		
//		usage.append("Examples:\n");
//		usage.append("    ehyo templates --list\n");
//		usage.append("    ehyo templates -l\n");
//		usage.append("    ehyo templates -a EmptyActivity\n");
//		usage.append("    ehyo templates --info Service\n");
//
//		usage.append("Options:\n");
//		usage.append("    -l, --list\n");
//		usage.append("        List all supported templates.\n");
//		usage.append("    -a, --apply <template name>\n");
//		usage.append("        Apply the given template to the project.\n");
//		usage.append("    -i, --info <template name>\n");
//		usage.append("        Display information regarding the given template.\n");		
		
		
		usage.append("usage: ehyo templates [-l | --location <template directory>]\n");
		usage.append("                      [-a | --apply]\n\n");
		
		usage.append("Examples:\n");
		usage.append("    ehyo templates --location <directory>\n");
		usage.append("    ehyo templates --location <directory> -a\n");
		usage.append("    ehyo templates --location <directory> --apply\n");

		usage.append("Options:\n");
		usage.append("    -l, --location <template directory>\n");
		usage.append("        Read the template from a given location.\n");
		usage.append("        If none of the arguments below are given, this command will\n");
		usage.append("        output information regarding the given template\n");
		usage.append("    -a, --apply\n");
		usage.append("        Modifier to the --location argument, it applies\n"); 
		usage.append("        the given template to the project.\n");
		
		return usage.toString();
	}
	
	@Override
	public void execute(List<String> args, Service service) {
		if ( args.isEmpty() )
			throw new UsageException(usage());
		
		boolean containsLocation = args.contains("-l") || args.contains("--location");

		if ( ! containsLocation )
			throw new UsageException(usage());
		
		if ( containsLocation ) {
			handleInfoFromLocation(args, service);
		}
	}
	
	private void handleInfoFromLocation(List<String> args, Service service) {
		String templateLocation = getArgValue("-l", args);
		if ( Strings.isNullOrEmpty(templateLocation) ) {
			templateLocation = getArgValue("--location", args);
			
			if ( Strings.isNullOrEmpty(templateLocation) ) {
				throw new UsageException("'-l, --location' have a required argument\n" + usage());
			}
		}
		boolean containsApply = args.contains("-a") || args.contains("--apply");
		
		Template template = service.loadTemplateFromDisk(templateLocation);

		if ( containsApply ) {
			doApply(template, service);
		} else {
			doShowInfo(template, service);
		}
	}
	
	private void handleInfo(List<String> args, Service service) {
		String templateName = getArgValue("-i", args);
		if ( Strings.isNullOrEmpty(templateName) ) {
			templateName = getArgValue("--info", args);
			
			if ( Strings.isNullOrEmpty(templateName) ) {
				throw new UsageException("'-i, --info' have a required argument\n" + 
										 "Consider running with -l or --list to get a list of known templates.\n" + 
										 usage());
			}
		}
		
		List<TemplateItem> items = registry.find(templateName);
		if ( items.isEmpty() )
			throw new GentleMessageException("No tempalates found matching '" + templateName + "'");
		
		TemplateItem item = service.createSelector("Multiple templates with '" + templateName + "' were found.\nWhich one would you like to know about", TemplateItem.class)
				.selectOne(items);
		
		Template template = service.loadTemplate(item.getFullPath());
		doShowInfo(template, service);
	}

	private void doShowInfo(Template template, Service service) {
		TemplateInfo info = template.loadTemplateInformation();
		
		StringBuilder builder = new StringBuilder();
		builder.append(info.getName() + "\n");
		builder.append(createUnderline(info.getName()) + "\n");
		builder.append(info.getDescription() + "\n");
		
		out.println(builder.toString());
	}


	private void handleApply(List<String> args, Service service) {
		String templateName = getArgValue("-a", args);
		if ( Strings.isNullOrEmpty(templateName) ) {
			templateName = getArgValue("--apply", args);
			
			if ( Strings.isNullOrEmpty(templateName) ) {
				throw new UsageException("'-a, --apply' have a required argument\n" + 
						 				 "Consider running with -l or --list to get a list of known templates.\n" + 
						 				 usage());
			}
		}
		
		List<TemplateItem> items = registry.find(templateName);
		if ( items.isEmpty() )
			throw new GentleMessageException("No tempalates found matching '" + templateName + "'");
		
		TemplateItem item = service.createSelector("Multiple templates with '" + templateName + "' were found.\nWhich one would you like to apply", TemplateItem.class)
				.selectOne(items);
		Template template = service.loadTemplate(item.getFullPath());
		
		doApply(template, service);
	}

	private void doApply(Template template, Service service) {
		List<ProjectSourceSet> sourceSets = service.getSourceSets();
		OptionSelector<ProjectSourceSet> configSelector = service.createSelector("Which source set would you like to apply this template to?", ProjectSourceSet.class);
		ProjectSourceSet sourceSet = configSelector.selectOne(sourceSets);
		
		List<TemplateFileParameter> parameters = template.loadTemplateParameters(sourceSet);
		
		Questioner<DefaultQuestion, TemplateProperty> questioner = service.createPrompt(new AnswerFactory<DefaultQuestion, TemplateProperty>() {
			@Override
			public TemplateProperty apply(DefaultQuestion input, String value) {
				return new TemplateProperty(input.getId(), value, input.getType());
			}
		});
		
		out.println("The template requires a few paraters.\nDefault values are displayed between []:");
		List<TemplateProperty> prompt = questioner.prompt(wrap(parameters));
		
		sourceSet.applyTemplate(template, prompt);
	}

	private List<DefaultQuestion> wrap(List<TemplateFileParameter> parameters) {
		ImmutableList.Builder<DefaultQuestion> questions = ImmutableList.builder();
		
		for ( TemplateFileParameter param : parameters )
			questions.add(new DefaultQuestion(param));

		return questions.build();
	}
	
	private void handleList() {
		List<TemplateItem> templates = registry.find();
		if ( templates.isEmpty() ) {
			throw new GentleMessageException("No templates found");
		}
		
		StringBuilder message = new StringBuilder();
		String header = "Templates Found";
		message.append(header + "\n");
		message.append(createUnderline(header) + "\n");

		for ( TemplateItem item : templates ) {
			message.append("    " + item.getName() + "\n");
		}
		
		out.println(message.toString());
	}

	private String createUnderline(String header) {
		StringBuilder underline = new StringBuilder();
		for ( int count = 0; count < header.length(); count++ ) {
			underline.append("-");
		}

		return underline.toString();
	}
	
	private static String getArgValue(String arg, List<String> args) {
		int libIndex = args.indexOf(arg);
		
		if (libIndex == -1) {
			return null;
		}
		
		int libValueIndex = libIndex + 1;
		if ( libValueIndex >= args.size() )
			return null;
		
		return args.get(libValueIndex);
	}

	@VisibleForTesting
	static class DefaultQuestion implements Question {
		private final TemplateFileParameter parameters;

		public DefaultQuestion( TemplateFileParameter parameters ) {
			this.parameters = parameters;
		}

		public String getId() {
			return parameters.getId();
		}

		@Override
		public String getType() {
			return parameters.getType();
		}
		
		@Override
		public String getValue() {
			return parameters.getDefaultValue();
		}

		@Override
		public Map<String, String> getOptions() {
			return parameters.getOptions();
		}
		
		@Override
		public String getName() {
			StringBuilder name = new StringBuilder();
			
			if (Strings.isNullOrEmpty(parameters.getName()))
				name.append(parameters.getId());
			else
				name.append(parameters.getName());
			
			if (! Strings.isNullOrEmpty(parameters.getHelp()))
					name.append("\n" + parameters.getHelp());
					
			return name.toString();
		}
	}	
}

