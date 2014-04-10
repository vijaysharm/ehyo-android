package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class GradleBuildChangeManager implements ChangeManager<GradleBuildDocument> {
	private final Map<GradleBuild, GradleBuildDocument> buildFiles;
	private final ProjectRegistry registry;
	
	public GradleBuildChangeManager(ProjectRegistry registry, Set<InternalBuildAction> buildActions) {
		this.registry = registry;
		this.buildFiles = transform(registry, buildActions);
	}

	@Override
	public void apply(PluginActionHandler<GradleBuildDocument> handler) {
		for ( Map.Entry<GradleBuild, GradleBuildDocument> entry : buildFiles.entrySet() ) {
			handler.modify(entry.getValue());
		}		
	}
	
	@Override
	public void commit(boolean dryrun) {
		
	}
	
	private static Map<GradleBuild, GradleBuildDocument> transform(ProjectRegistry registry, Set<InternalBuildAction> actions) {
		ImmutableMap.Builder<GradleBuild, GradleBuildDocument> map = ImmutableMap.builder();
		for ( InternalBuildAction action : actions ) {
			Set<String> gradleIds = action.getAddedDependencies().keySet();
			for ( String id : gradleIds ) {
				GradleBuild build = registry.getGradleBuild(id);
				map.put(build, build.asDocument());
			}
		}
		
		return map.build();
	}
}
