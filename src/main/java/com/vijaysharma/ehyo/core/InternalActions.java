package com.vijaysharma.ehyo.core;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectManifest;

class InternalActions {
	static class InternalManifestAction implements ManifestAction {
		private ImmutableMultimap.Builder<String, String> addedPermissions = ImmutableMultimap.builder();
		
		@Override
		public void addPermission(ProjectManifest manifest, String permission) {
			DefaultProjectManifest projectManifest = (DefaultProjectManifest) manifest;
			addedPermissions.put(projectManifest.getManifest().getId(), permission);
		}
		
		public Multimap<String, String> getAddedPermissions() {
			return addedPermissions.build();
		}
	}
	
	static class InternalBuildAction implements BuildAction {
		private ImmutableMultimap.Builder<String, BuildActionDependencyValue> addedDependencies = ImmutableMultimap.builder();
		
		@Override
		public void addDependency(BuildConfiguration config, String dependency) {
			DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
			BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
			addedDependencies.put(configuration.getBuild().getId(), value);
		}
		
		public Multimap<String, BuildActionDependencyValue> getAddedDependencies() {
			return addedDependencies.build();
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
