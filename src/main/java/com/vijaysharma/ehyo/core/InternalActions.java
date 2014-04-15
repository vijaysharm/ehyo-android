package com.vijaysharma.ehyo.core;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;

class InternalActions {
	static class ManifestActions implements PluginAction {
		private final ImmutableMultimap.Builder<String, String> addedPermissions = ImmutableMultimap.builder();
		private final ImmutableMultimap.Builder<String, String> removedPermissions = ImmutableMultimap.builder();
		
		public void addPermission(AndroidManifest manifest, String permission) {
			addedPermissions.put(manifest.getId(), permission);
		}
		
		public Multimap<String, String> getAddedPermissions() {
			return addedPermissions.build();
		}
		
		public void removePermission(AndroidManifest manifest, String permission) {
			removedPermissions.put(manifest.getId(), permission);
		}
		
		public Multimap<String, String> getRemovedPermissions() {
			return removedPermissions.build();
		}
		
		public boolean hasChanges() {
			return  (! addedPermissions.build().isEmpty()) || (! removedPermissions.build().isEmpty() );
		}

		// TODO: Return an interface that is only applied to a single manifest
		public ManifestActions from(AndroidManifest key) {
			return this;
		}
	}
	
	static class BuildActions implements PluginAction {
		private final ImmutableMultimap.Builder<String, BuildActionDependencyValue> addedDependencies = ImmutableMultimap.builder();
		private final ImmutableMultimap.Builder<String, BuildActionDependencyValue> removedDependencies = ImmutableMultimap.builder();
		
		public void addDependency(BuildConfiguration config, String dependency) {
			DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
			BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
			addedDependencies.put(configuration.getBuild().getId(), value);
		}
		
		public Multimap<String, BuildActionDependencyValue> getAddedDependencies() {
			return addedDependencies.build();
		}
		
		public void removeDependency(BuildConfiguration config, String dependency) {
			DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
			BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
			removedDependencies.put(configuration.getBuild().getId(), value);
		}
		
		public Multimap<String, BuildActionDependencyValue> getRemovedDependencies() {
			return removedDependencies.build();
		}

		public boolean hasChanges() {
			return  (! addedDependencies.build().isEmpty()) || (! removedDependencies.build().isEmpty() );
		}

		// TODO: Return an interface that is only applied to a single manifest
		public BuildActions from(GradleBuild key) {
			return this;
		}
	}
	
	static class DefaultPluginActions implements PluginAction {
		
	}
	
	static class BuildActionDependencyValue {
		private final DefaultBuildConfiguration configuration;
		private final String dependency;
		
		public BuildActionDependencyValue(DefaultBuildConfiguration config, String dependency) {
			this.configuration = config;
			this.dependency = dependency;
		}
		
		public String getDependency() {
			return dependency;
		}
		
		public DefaultBuildConfiguration getConfiguration() {
			return configuration;
		}
	}
}
