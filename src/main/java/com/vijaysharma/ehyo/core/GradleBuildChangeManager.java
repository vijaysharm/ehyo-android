package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class GradleBuildChangeManager implements ChangeManager<PluginActions> {
	private static final Function<GradleBuild, String> BUILD_FILE_RENDERER = new Function<GradleBuild, String>() {
		@Override
		public String apply(GradleBuild build) {
			return build.getProject() + ":" + build.getFile().getName();
		}
	};
	
	static class GradleBuildChangeManagerFactory {
		GradleBuildChangeManager create(ProjectRegistry registry) {
			return new GradleBuildChangeManager(registry, new PatchApplier<GradleBuild, GradleBuildDocument>(BUILD_FILE_RENDERER));
		}	
	}
	
	private final ImmutableMap.Builder<GradleBuild, GradleBuildDocument> buildFiles;
	private final PatchApplier<GradleBuild, GradleBuildDocument> patcher;
	private final ProjectRegistry registry;
	private final PluginActionHandlerFactory factory;
	
	GradleBuildChangeManager(ProjectRegistry registry, 
							 PatchApplier<GradleBuild, GradleBuildDocument> applier,
							 PluginActionHandlerFactory factory) {
		this.buildFiles = ImmutableMap.builder();
		this.patcher = applier;
		this.registry = registry;
		this.factory = factory;
	}

	public GradleBuildChangeManager(ProjectRegistry registry,
									PatchApplier<GradleBuild, GradleBuildDocument> patcher) {
		this(registry, patcher, new PluginActionHandlerFactory());
	}

	@Override
	public void apply(PluginActions actions) {
		Set<String> gradleIds = actions.getAddedDependencies().keySet();
		for ( String id : gradleIds ) {
			GradleBuild build = registry.getGradleBuild(id);
			buildFiles.put(build, build.asDocument());
		}
		
		BuildActionHandler handler = factory.createBuildActionHandler();
		for ( Map.Entry<GradleBuild, GradleBuildDocument> entry : buildFiles.build().entrySet() ) {
			handler.modify(entry.getValue(), actions.getBuildActions(entry.getKey()));
		}		
	}
	
	@Override
	public void commit(boolean dryrun) {
		patcher.apply(buildFiles.build(), dryrun);
	}	
}
