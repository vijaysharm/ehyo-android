package com.vijaysharma.ehyo.core;

import static com.google.common.base.Joiner.on;

import java.util.Set;

import com.google.common.base.Function;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.OptionSelectorFactory;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;

class RunActionInternals {

	static class DefaultBuildConfiguration implements BuildConfiguration {
		private final GradleBuild build;
		private final BuildType buildType;
		private final Flavor flavor;
		private final InternalBuildAction buildAction;
		
		public DefaultBuildConfiguration(BuildType buildType, Flavor flavor, GradleBuild build, InternalBuildAction buildAction) {
			this.flavor = flavor;
			this.build = build;
			this.buildType = buildType;
			this.buildAction = buildAction;
		}
		
		@Override
		public void addDependency(String projectId) {
			buildAction.addDependency(this, projectId);
		}
		
		public BuildType getBuildType() {
			return buildType;
		}
		
		public GradleBuild getBuild() {
			return build;
		}
		
		public Flavor getFlavor() {
			return flavor;
		}
		
		public InternalBuildAction getBuildAction() {
			return buildAction;
		}

		@Override
		public String toString() {
			if ( flavor == null ) {
				return on(":").join(build.getProject(), buildType.getCompileString());
			} else {
				return on(":").join(build.getProject(), flavor.getCompileString(buildType));	
			}
		}
	}
	
	static class DefaultProjectManifest implements ProjectManifest {
		private final AndroidManifest manifest;
		private final InternalManifestAction manifestActions;
		
		public DefaultProjectManifest(AndroidManifest manifest, InternalManifestAction manifestActions) {
			this.manifest = manifest;
			this.manifestActions = manifestActions;
		}
		
		@Override
		public void addPermissions(Set<String> permissions) {
			for ( String permission : permissions ) {
				manifestActions.addPermission(this, permission);
			}
		}
		
		@Override
		public void removePermissions(Set<String> permissions) {
			for ( String permission : permissions ) {
				manifestActions.removePermission(this, permission);
			}
		}
		
		@Override
		public Set<String> getPermissions() {
			return manifest.asDocument().getPermissions();
		}
		
		public AndroidManifest getManifest() {
			return manifest;
		}
		
		public InternalManifestAction getManifestActions() {
			return manifestActions;
		}
		
		@Override
		public String toString() {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	}

	static class DefaultProjectBuild implements ProjectBuild {
		private final GradleBuild build;
		public DefaultProjectBuild(GradleBuild build) {
			this.build = build;
		}
	}
	
	static class DefaultOptionSelectorFactory implements OptionSelectorFactory {
		@Override
		public <T> OptionSelector<T> create(Class<T> clazz) {
			String header = "Select";
			return new OptionSelector<T>(header, new ToStringRenderer<T>());
		}
	}
	
	private static class ToStringRenderer<T> implements Function<T, String> {
		@Override
		public String apply(T input) {
			return input.toString();
		}
	}
}
