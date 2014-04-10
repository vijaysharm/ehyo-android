package com.vijaysharma.ehyo.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class GradleBuildChangeManager implements ChangeManager<GradleBuildDocument> {
	private static final Function<GradleBuild, String> BUILD_FILE_RENDERER = new Function<GradleBuild, String>() {
		@Override
		public String apply(GradleBuild build) {
			return build.getProject() + ":" + build.getFile().getName();
		}
	};
	
	static class GradleBuildChangeManagerFactory {
		GradleBuildChangeManager create(ProjectRegistry registry, Set<InternalBuildAction> buildActions) {
			return new GradleBuildChangeManager(transform(registry, buildActions), BUILD_FILE_RENDERER);
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
	private final Function<GradleBuild, String> renderer;
	
	private GradleBuildChangeManager(Map<GradleBuild, GradleBuildDocument> buildFiles, Function<GradleBuild, String> renderer) {
		this.buildFiles = buildFiles;
		this.renderer = renderer;
	}

	@Override
	public void apply(PluginActionHandler<GradleBuildDocument> handler) {
		for ( Map.Entry<GradleBuild, GradleBuildDocument> entry : buildFiles.entrySet() ) {
			handler.modify(entry.getValue());
		}		
	}
	
	@Override
	public void commit(boolean dryrun) {
		for ( Map.Entry<GradleBuild, GradleBuildDocument> build : buildFiles.entrySet() ) {
			try {
				List<String> baseline = toListOfStrings(build.getKey().asDocument());
				List<String> changed = toListOfStrings(build.getValue());
				Patch diff = DiffUtils.diff(baseline, changed);
				if ( ! diff.getDeltas().isEmpty() ) {
					if ( dryrun ) {
						show(build.getKey(), diff);
					} else {
						save(build.getKey(), build.getValue());
					}					
				}
			} catch (Exception ex) {
				// TODO: What to do here?
			}
		}
	}
	
	private static List<String> toListOfStrings(GradleBuildDocument document) {
		return document.asListOfStrings();
	}
	
	private void show(GradleBuild build, Patch diff) throws IOException {		
		Outputter.out.println("Diff " + renderer.apply(build));
		for (Delta delta: diff.getDeltas()) {
			Outputter.out.println(delta);
		}		
	}

	private void save(GradleBuild build, GradleBuildDocument modified) throws IOException {
		Outputter.out.print("Writing " + renderer.apply(build) + "... ");
		List<String> changed = toListOfStrings(modified);
		EFileUtil.write(build, changed);
		Outputter.out.println("done");
	}
}
