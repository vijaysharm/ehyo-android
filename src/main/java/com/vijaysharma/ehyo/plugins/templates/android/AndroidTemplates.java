package com.vijaysharma.ehyo.plugins.templates.android;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class AndroidTemplates implements Plugin {
	@Override
	public String name() {
		return "android-templates";
	}

	@Override
	public void execute(List<String> args, Service service) {
		List<ProjectSourceSet> sourceSets = gather(service);
		OptionSelector<ProjectSourceSet> configSelector = service.createSelector(ProjectSourceSet.class);
		List<ProjectSourceSet> selectedBuildConfigs = configSelector.select(sourceSets, false);

		String templatePath = "/templates/other/Service";
		List<TemplateParameters> parameters = service.loadTemplateParameters(templatePath);
		for ( ProjectSourceSet sourceSet : selectedBuildConfigs ) {
			sourceSet.applyTemplate(templatePath, parameters);
		}
	}
	
	private List<ProjectSourceSet> gather(Service service) {
		ImmutableList.Builder<ProjectSourceSet> configs = ImmutableList.builder();
		for( ProjectBuild build : service.getProjectBuilds() ) {
			configs.addAll(build.getSourceSets());
		}

		return configs.build();
	}
}
