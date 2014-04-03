package com.vijaysharma.ehyo.core;

import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;

class InternalManifestAction implements ManifestAction {
	private List<String> permissions = Lists.newArrayList();

	@Override
	public void addPermission(String permission) {
		this.permissions.add(permission);
	}
	
	public List<String> getAddedPermissions() {
		return permissions;
	}
}
