package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.InternalActions.BuildActionDependencyValue;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
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
		Outputter.out.print("Found: " + libraries.size() + " values");
//		dependencies.g
	}
}
