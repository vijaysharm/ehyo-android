package com.vijaysharma.ehyo.core;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.OptionSelectorFactory;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;

class RunActionInternals {

	static class DefaultBuildConfiguration implements BuildConfiguration {
		private final GradleBuild build;
		private final BuildType buildType;
		private final Flavor flavor;
		private final PluginActions actions;
		
		public DefaultBuildConfiguration(BuildType buildType,
										 Flavor flavor,
										 GradleBuild build, 
										 PluginActions actions) {
			this.flavor = flavor;
			this.build = build;
			this.buildType = buildType;
			this.actions = actions;
		}
		
		@Override
		public void addDependency(String projectId) {
			actions.addDependency(build, buildType, flavor, projectId);
		}
		
		@Override
		public Set<String> getDependencies() {
			return build.getDependencies(buildType, flavor);
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
			return Joiner.on(":").join(build.getProject(), 
									   buildType.getCompileString(flavor));
		}
	}
	
	static class DefaultProjectManifest implements ProjectManifest {
		private final AndroidManifest manifest;
		private final PluginActions actions;
		
		public DefaultProjectManifest(AndroidManifest manifest, PluginActions actions) {
			this.manifest = manifest;
			this.actions = actions;
		}
		
		@Override
		public void addPermissions(Set<String> permissions) {
			for ( String permission : permissions ) {
				actions.addPermission(manifest, permission);
			}
		}
		
		@Override
		public void removePermissions(Set<String> permissions) {
			for ( String permission : permissions ) {
				actions.removePermission(manifest, permission);
			}
		}
		
		@Override
		public Set<String> getPermissions() {
			return manifest.getPermissions();
		}
		
		@Override
		public String toString() {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	}

	static class DefaultProjectBuild implements ProjectBuild {
		private final GradleBuild build;
		private final PluginActions actions;
		
		public DefaultProjectBuild(GradleBuild build, PluginActions actions) {
			this.build = build;
			this.actions = actions;
		}
		
		public GradleBuild getBuild() {
			return build;
		}

		@Override
		public Set<String> getFlavors() {
			ImmutableSet.Builder<String> flavors = ImmutableSet.builder();
			for ( Flavor flavor : build.getFlavors() ) 
				flavors.add(flavor.getFlavor());

			return flavors.build();
		}

		@Override
		public Set<String> getBuildTypes() {
			ImmutableSet.Builder<String> buildtype = ImmutableSet.builder();
			for ( BuildType type : build.getBuildTypes() ) 
				buildtype.add(type.getType());

			return buildtype.build();
		}

		@Override
		public Set<BuildConfiguration> getBuildConfigurations() {
			ImmutableSet.Builder<BuildConfiguration> buildtype = ImmutableSet.builder();
			
			for ( Dependency dependency : build.getDependencies() ) {
				buildtype.add(new DefaultBuildConfiguration(dependency.getBuildType(), 
															dependency.getFlavor(), 
															build, 
															actions));
			}
			
			return buildtype.build();
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
