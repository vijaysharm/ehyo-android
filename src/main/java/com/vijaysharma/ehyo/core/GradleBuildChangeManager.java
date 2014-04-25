package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;

public class GradleBuildChangeManager implements ChangeManager<PluginActions> {
	private static final Function<GradleBuild, GradleBuildDocument> PRODUCER = new Function<GradleBuild, GradleBuildDocument>() {
		@Override
		public GradleBuildDocument apply(GradleBuild input) {
			return GradleBuildDocument.read(input.getFile());
		}
	};
	
	// TODO: Replace with ObjectFactory
	static class GradleBuildChangeManagerFactory {
		GradleBuildChangeManager create() {
			
			return new GradleBuildChangeManager(new PatchApplier<GradleBuild, GradleBuildDocument>(PRODUCER));
		}	
	}
	
	private final ImmutableMap.Builder<GradleBuild, GradleBuildDocument> buildFiles;
	private final PatchApplier<GradleBuild, GradleBuildDocument> patcher;
	private final Function<GradleBuild, GradleBuildDocument> producer;
	private final PluginActionHandlerFactory factory;
	
	GradleBuildChangeManager(PatchApplier<GradleBuild, GradleBuildDocument> applier,
							 PluginActionHandlerFactory factory,
							 Function<GradleBuild, GradleBuildDocument> producer ) {
		this.buildFiles = ImmutableMap.builder();
		this.patcher = applier;
		this.factory = factory;
		this.producer = producer;
	}

	public GradleBuildChangeManager(PatchApplier<GradleBuild, GradleBuildDocument> patcher) {
		this(patcher, new PluginActionHandlerFactory(), PRODUCER);
	}

	@Override
	public void apply(PluginActions actions) {
		// TODO: Don't just look at the ADDED dependencies
		Set<GradleBuild> builds = Sets.newHashSet(actions.getAddedDependencies().keySet());
		builds.addAll(actions.getRemovedDependencies().keySet());
		for ( GradleBuild build : builds ) {
			buildFiles.put(build, producer.apply(build));
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
