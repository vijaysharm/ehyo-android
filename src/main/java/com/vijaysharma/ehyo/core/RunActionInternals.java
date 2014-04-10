package com.vijaysharma.ehyo.core;

import static com.google.common.base.Joiner.on;

import com.google.common.base.Function;
import com.vijaysharma.ehyo.api.ActionFactories.BuildActionFactory;
import com.vijaysharma.ehyo.api.ActionFactories.ManifestActionFactory;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.ManifestAction;
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
		
		public DefaultBuildConfiguration(BuildType buildType, Flavor flavor, GradleBuild build) {
			this.flavor = flavor;
			this.build = build;
			this.buildType = buildType;
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
		public DefaultProjectManifest(AndroidManifest manifest) {
			this.manifest = manifest;
		}
	}

	static class DefaultProjectBuild implements ProjectBuild {
		private final GradleBuild build;
		public DefaultProjectBuild(GradleBuild build) {
			this.build = build;
		}
	}
	
	static class DefaultBuildActionFactory implements BuildActionFactory {
		@Override
		public BuildAction create() {
			return new InternalBuildAction();
		}
	}
	
	static class DefaultManifestActionFactory implements ManifestActionFactory {
		@Override
		public ManifestAction create() {
			return new InternalManifestAction();
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
