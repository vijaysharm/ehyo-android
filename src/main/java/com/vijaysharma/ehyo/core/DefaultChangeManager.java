package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.core.PluginActions.TemplateAction;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.SourceSet;

public class DefaultChangeManager implements ChangeManager<PluginActions> {

	private final ProjectRegistry registry;

	public DefaultChangeManager(ProjectRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void apply(PluginActions actions) {
		Set<TemplateAction> templates = actions.getTemplates();
		for ( TemplateAction action : templates ) {
			perform(action);
		}
	}

	@Override
	public void commit(boolean dryrun) {

	}
	
	private void perform(TemplateAction action) {
		SourceSet sourceSet = action.getSourceSet();
		DefaultTemplate template = action.getTemplate();
		Project project = registry.getProject(sourceSet.getProject());
		GradleBuild build = project.getBuild();
		AndroidManifest manifest = sourceSet.getManifests();
		
		final Map<String, Object> mapping = Maps.newHashMap();
		for ( TemplateParameters param : action.getParameters() ) {
			mapping.put(param.getId(), param.getDefaultValue());
		}
		
		// TODO: This might be acceptable until ehyo supports creating new projects
		mapping.put("isNewProject", false);
		mapping.put("buildApi", 16);
		mapping.put("minApiLevel", 16);
		
		mapping.put("manifestDir", manifest.getFile().getParentFile().getAbsolutePath());
		mapping.put("srcDir", manifest.getSourceDirectory().getAbsolutePath());
		mapping.put("resDir", manifest.getResourceDirectory().getAbsolutePath());
		mapping.put("packageName", manifest.getPackageName());

		template.getActions(mapping);
	}
}
