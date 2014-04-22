package com.vijaysharma.ehyo.plugins.templates.android;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectBuild;
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
		List<BuildConfiguration> buildConfigs = gather(service);
		OptionSelector<BuildConfiguration> configSelector = service.createSelector(BuildConfiguration.class);
		List<BuildConfiguration> selectedBuildConfigs = configSelector.select(buildConfigs, false);

		List<TemplateParameters> parameters = Lists.newArrayList();
		for ( BuildConfiguration config : selectedBuildConfigs ) {
			config.applyTemplate("/path/to/template", parameters);
		}
	}
	
	private List<BuildConfiguration> gather(Service service) {
		ImmutableList.Builder<BuildConfiguration> configs = ImmutableList.builder();
		for( ProjectBuild build : service.getProjectBuilds() ) {
			configs.addAll(build.getBuildConfigurations());
		}

		return configs.build();
	}
}
