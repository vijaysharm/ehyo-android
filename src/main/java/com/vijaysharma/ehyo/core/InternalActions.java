package com.vijaysharma.ehyo.core;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.BuildAction;
import com.vijaysharma.ehyo.api.ManifestAction;

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
		
	}
}
