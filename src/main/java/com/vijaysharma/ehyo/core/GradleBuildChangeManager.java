package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class GradleBuildChangeManager implements ChangeManager<GradleBuildDocument> {
	private static final Function<GradleBuild, String> BUILD_FILE_RENDERER = new Function<GradleBuild, String>() {
		@Override
		public String apply(GradleBuild build) {
			return build.getProject() + ":" + build.getFile().getName();
		}
	};
	
	static class GradleBuildChangeManagerFactory {
		GradleBuildChangeManager create(ProjectRegistry registry, Set<InternalBuildAction> buildActions) {
			PatchApplier<GradleBuild, GradleBuildDocument> patcher = new PatchApplier<GradleBuild, GradleBuildDocument>(BUILD_FILE_RENDERER);
			return new GradleBuildChangeManager(transform(registry, buildActions), patcher);
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
	
	private final Map<GradleBuild, GradleBuildDocument> buildFiles;
	private final PatchApplier<GradleBuild, GradleBuildDocument> patcher;
	
	GradleBuildChangeManager(Map<GradleBuild, GradleBuildDocument> buildFiles, PatchApplier<GradleBuild, GradleBuildDocument> applier ) {
		this.buildFiles = buildFiles;
		this.patcher = applier;
	}

	@Override
	public void apply(PluginActionHandler<GradleBuildDocument> handler) {
		for ( Map.Entry<GradleBuild, GradleBuildDocument> entry : buildFiles.entrySet() ) {
			handler.modify(entry.getValue());
		}		
	}
	
	@Override
	public void commit(boolean dryrun) {
		patcher.apply(buildFiles, dryrun);
	}
}
