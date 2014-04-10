package com.vijaysharma.ehyo.core;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.models.GradleBuild;

class InternalActions {
	static class InternalManifestAction implements ManifestAction {
		private ImmutableList.Builder<String> addedPermissions = ImmutableList.builder();
		private ImmutableList.Builder<String> removedPermissions = ImmutableList.builder();

		@Override
		public void addPermission(String permission) {
			this.addedPermissions.add(permission);
		}
		
		public List<String> getAddedPermissions() {
			return addedPermissions.build();
		}
		
		@Override
		public void removePermission(String permission) {
			this.removedPermissions.add(permission);
		}
		
		public List<String> getRemovedPermissions() {
			return removedPermissions.build();
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
	}
}
