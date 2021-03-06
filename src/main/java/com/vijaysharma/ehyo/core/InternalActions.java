package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.List;

import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.ManifestTags.Activity;
import com.vijaysharma.ehyo.core.models.ManifestTags.Receiver;
import com.vijaysharma.ehyo.core.models.ManifestTags.Service;
import com.vijaysharma.ehyo.core.models.ResourceDocument;

class InternalActions {
	static interface ManifestActions {
		Collection<String> getAddedPermissions();
		Collection<String> getRemovedPermissions();
		Collection<Receiver> getAddedReceivers();
		Collection<Service> getAddedServices();
		Collection<Activity> getAddedActivities();
	}
	
	static interface BuildActions {
		Collection<Dependency> getAddedDependencies();
		Collection<Dependency> getRemovedDependencies();
	}
	
	static interface FileActions {
		Collection<List<String>> getCreatedFiles();
	}
	
	static interface ResourceActions {
		Collection<ResourceDocument> getMergedResources();
	}
}
