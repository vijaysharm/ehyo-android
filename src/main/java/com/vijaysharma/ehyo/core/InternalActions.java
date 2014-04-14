package com.vijaysharma.ehyo.core;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectManifest;

class InternalActions {
	static class InternalManifestAction implements PluginAction {
		private final ImmutableMultimap.Builder<String, String> addedPermissions = ImmutableMultimap.builder();
		private final ImmutableMultimap.Builder<String, String> removedPermissions = ImmutableMultimap.builder();
		
		public void addPermission(ProjectManifest manifest, String permission) {
			DefaultProjectManifest projectManifest = (DefaultProjectManifest) manifest;
			addedPermissions.put(projectManifest.getManifest().getId(), permission);
		}
		
		public Multimap<String, String> getAddedPermissions() {
			return addedPermissions.build();
		}
		
		public void removePermission(ProjectManifest manifest, String permission) {
			DefaultProjectManifest projectManifest = (DefaultProjectManifest) manifest;
			removedPermissions.put(projectManifest.getManifest().getId(), permission);
		}
		
		public Multimap<String, String> getRemovedPermissions() {
			return removedPermissions.build();
		}
		
		public boolean hasChanges() {
			return  (! addedPermissions.build().isEmpty()) || (! removedPermissions.build().isEmpty() );
		}
	}
	
	static class InternalBuildAction implements PluginAction {
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
