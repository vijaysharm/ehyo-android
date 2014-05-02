package com.vijaysharma.ehyo.core;

import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;
import com.vijaysharma.ehyo.api.OptionSelectorFactory;
import com.vijaysharma.ehyo.api.ProjectBuild;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.api.TemplateFactory;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.SourceSet;

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
		public void addPermission(String permission) {
			actions.addPermission(manifest, permission);
		}
		
		@Override
		public void addPermissions(Set<String> permissions) {
			for ( String permission : permissions ) {
				actions.addPermission(manifest, permission);
			}
		}
		
		@Override
		public void removePermission(String permission) {
			actions.removePermission(manifest, permission);
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
			return manifest.toString();
		}
	}

	static class DefaultProjectBuild implements ProjectBuild {
		private final GradleBuild build;
		private final PluginActions actions;
		private final ProjectRegistry registry;
		
		public DefaultProjectBuild(GradleBuild build, PluginActions actions, ProjectRegistry registry) {
			this.build = build;
			this.actions = actions;
			this.registry = registry;
		}
		
		public GradleBuild getBuild() {
			return build;
		}

		@Override
		public Set<Flavor> getFlavors() {
			return build.getFlavors();
		}

		@Override
		public Set<BuildType> getBuildTypes() {
			return build.getBuildTypes();
		}

		@Override
		public Set<BuildConfiguration> getBuildConfigurations() {
			ImmutableSet.Builder<BuildConfiguration> buildtype = ImmutableSet.builder();
			
			for ( BuildType type : build.getBuildTypes() ) {
				buildtype.add(create(type, null));

				for ( Flavor flavor : build.getFlavors() ) {
					buildtype.add(create(type, flavor));
				}
			}
			
			return buildtype.build();
		}

		@Override
		public Set<ProjectSourceSet> getSourceSets() {
			ImmutableSet.Builder<ProjectSourceSet> sourceSets = ImmutableSet.builder();

			for ( SourceSet sourceSet : build.getSourceSets() ) {
				// TODO: I'm only doing this filter so that I filter out any
				// source sets that don't have manifests
				if ( sourceSet.getManifests() != null ) {
					sourceSets.add(new DefaultProjectSourceSet(sourceSet, actions, registry));
				}
			}
			
			return sourceSets.build();
		}
		
		@Override
		public Set<ProjectManifest> getManifests() {
			ImmutableSet.Builder<ProjectManifest> manifests = ImmutableSet.builder();
			
			for ( SourceSet sourceSet : build.getSourceSets() ) {
				// TODO: I'm only doing this filter so that I filter out any
				// source sets that don't have manifests
				if ( sourceSet.getManifests() != null ) {
					manifests.add(new DefaultProjectManifest(sourceSet.getManifests(), actions));
				}
			}
				
			return manifests.build();
		}

		private DefaultBuildConfiguration create(BuildType type, Flavor flavor) {
			return new DefaultBuildConfiguration(type, flavor, build, actions);
		}
	}
	
	static class DefaultProjectSourceSet implements ProjectSourceSet {
		private final PluginActions actions;
		private final SourceSet sourceSet;
		private final ProjectRegistry registry;

		public DefaultProjectSourceSet(SourceSet sourceSet, PluginActions actions, ProjectRegistry registry) {
			this.sourceSet = sourceSet;
			this.actions = actions;
			this.registry = registry;
		}
		
		@Override
		public void applyTemplate(Template template, List<TemplateParameters> parameters) {
			DefaultTemplate tmpl = (DefaultTemplate) template;
			actions.applyTemplate(tmpl, sourceSet, parameters, registry);
		}
		
		@Override
		public String toString() {
			return Joiner.on(":").join(sourceSet.getProject(), sourceSet.getSourceSet().getType());
		}
	}
	
	static class DefaultTemplateFactory implements TemplateFactory {
		@Override
		public Template create(String path) {
			return new DefaultTemplate(path);
		}
	}
	
	static class DefaultOptionSelectorFactory implements OptionSelectorFactory {
		@Override
		public <T> OptionSelector<T> create(String header, Class<T> clazz) {
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
