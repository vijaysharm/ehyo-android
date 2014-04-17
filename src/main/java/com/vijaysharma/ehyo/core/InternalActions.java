package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.vijaysharma.ehyo.core.PluginActions.BuildActionDependencyValue;

class InternalActions {
	static interface ManifestActions {
		Collection<String> getAddedPermissions();
		Collection<String> getRemovedPermissions();
	}
	
	static interface BuildActions {
		Collection<BuildActionDependencyValue> getAddedDependencies();
	}
}
