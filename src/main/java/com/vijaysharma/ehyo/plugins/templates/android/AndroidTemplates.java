package com.vijaysharma.ehyo.plugins.templates.android;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class AndroidTemplates implements Plugin {
	@Override
	public String name() {
		return "android-templates";
	}

	String[] templates = {
//		"/templates/activities/MapFragmentMasterDetail",
//		"/templates/activities/SherlockBlankActivity",
//		"/templates/activities/SherlockMasterDetailFlow",
//		"/templates/activities/SlidingPaneMasterDetailFlow",
//		"/templates/activities/TVLeftNavBarActivity",
//
//		"/templates/activities/BlankActivity",
//		"/templates/activities/EmptyActivity",
//		"/templates/activities/FullscreenActivity",
//		"/templates/activities/GoogleMapsActivity", // Does not work "projectOut"
//		"/templates/activities/GooglePlayServicesActivity",
//		"/templates/activities/LoginActivity",
//		"/templates/activities/MasterDetailFlow", // Does not work "minApiLevel"
//		"/templates/activities/SettingsActivity",
//
//		"/templates/other/AppWidget",
//		"/templates/other/BlankFragment",
//		"/templates/other/BroadcastReceiver",
//		"/templates/other/ContentProvider",
//		"/templates/other/CustomView",	// Doesn't work isLibraryProject && isGradle
//		"/templates/other/Daydream",
//		"/templates/other/IntentService",
//		"/templates/other/ListFragment",
//		"/templates/other/Notification",
		"/templates/other/PlusOneFragment",
		"/templates/other/Service",

		"/templates/other/EfficientListAdapter",
		"/templates/other/ParcelableType"
	};
	
	@Override
	public void execute(List<String> args, Service service) {
		List<ProjectSourceSet> sourceSets = gather(service);
		OptionSelector<ProjectSourceSet> configSelector = service.createSelector(ProjectSourceSet.class);
		List<ProjectSourceSet> selectedBuildConfigs = configSelector.select(sourceSets, false);

		String templatePath = templates[0];
		Template template = service.loadTemplate(templatePath);
		List<TemplateParameters> parameters = template.loadTemplateParameters();
		
		for ( ProjectSourceSet sourceSet : selectedBuildConfigs ) {
			sourceSet.applyTemplate(template, parameters);
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

