package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.vijaysharma.ehyo.core.models.Dependency;

class InternalActions {
	static interface ManifestActions {
		Collection<String> getAddedPermissions();
		Collection<String> getRemovedPermissions();
	}
	
	static interface BuildActions {
		Collection<Dependency> getAddedDependencies();
		Collection<Dependency> getRemovedDependencies();
	}
}
