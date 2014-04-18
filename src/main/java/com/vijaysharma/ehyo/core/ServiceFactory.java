package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultOptionSelectorFactory;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectBuild;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ServiceFactory {
	public Service create(PluginLoader pluginLoader, ProjectRegistry registry, final PluginActions actions) {
		Collection<Plugin> plugins = pluginLoader.transform(Functions.<Plugin>identity());
		
		List<ProjectBuild> builds = registry.getAllGradleBuilds(new Function<GradleBuild, ProjectBuild>() {
			@Override
			public ProjectBuild apply(GradleBuild build) {
				return new DefaultProjectBuild(build, actions);
			}
		});
		
		List<ProjectManifest> manifests = registry.getAllAndroidManifests(new Function<AndroidManifest, ProjectManifest>() {
			@Override
			public ProjectManifest apply(AndroidManifest manifest) {
				return new DefaultProjectManifest(manifest, actions);
			}
		});
		
		return new Service(plugins, 
						   manifests,
						   builds,
						   new DefaultOptionSelectorFactory());
	}
}
