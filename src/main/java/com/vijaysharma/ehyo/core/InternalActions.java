package com.vijaysharma.ehyo.core;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.BuildConfiguration;

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
		private ImmutableMultimap.Builder<BuildConfiguration, String> addedDependencies = ImmutableMultimap.builder();
		private ImmutableMultimap.Builder<BuildConfiguration, String> removedDependencies = ImmutableMultimap.builder();
		
		@Override
		public void addDependency(BuildConfiguration variant, String dependency) {
			addedDependencies.put(variant, dependency);
		}
		
		public Multimap<BuildConfiguration, String> getAddedDependencies() {
			return addedDependencies.build();
		}
		
		public void removeDependency(BuildConfiguration variant, String dependency) {
			removedDependencies.put(variant, dependency);
		}
		
		public Multimap<BuildConfiguration, String> getRemovedDependencies() {
			return removedDependencies.build();
		}
	}
}
