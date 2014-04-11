package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.core.InternalActions.BuildActionDependencyValue;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;

public class BuildActionHandler implements PluginActionHandler<GradleBuildDocument>{
	private final InternalBuildAction action;

	public BuildActionHandler(InternalBuildAction action) {
		this.action = action;
	}

	@Override
	public void modify(GradleBuildDocument document) {
		Multimap<String, BuildActionDependencyValue> dependencies = action.getAddedDependencies();
		Collection<BuildActionDependencyValue> libraries = dependencies.get(document.getGradleId());
		for ( BuildActionDependencyValue lib : libraries ) {
			String dependency = formatDependency( lib );
			document.appendDependency(dependency);
		}
	}

	private String formatDependency(BuildActionDependencyValue lib) {
		StringBuilder dependency = new StringBuilder();
		
		DefaultBuildConfiguration configuration = lib.getConfiguration();
		BuildType buildType = configuration.getBuildType();
		Flavor flavor = configuration.getFlavor();
		
		String compileString = flavor == null ? buildType.getCompileString() :  flavor.getCompileString(buildType);
		dependency.append(compileString).append(" \'" + lib.getDependency() + "\'");
		
		return dependency.toString();
	}
}