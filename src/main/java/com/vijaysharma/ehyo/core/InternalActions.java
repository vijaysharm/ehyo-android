package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.vijaysharma.ehyo.core.PluginActions.BuildActionDependencyValue;

class InternalActions {
	static interface ManifestActions {
		Collection<String> getAddedPermissions();
	}
	
	static interface BuildActions {
		Collection<BuildActionDependencyValue> getAddedDependencies();
	}
}
