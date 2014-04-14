package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestActionHandler implements PluginActionHandler<AndroidManifestDocument> {
	
	private final InternalManifestAction action;

	public ManifestActionHandler(InternalManifestAction action) {
		this.action = action;
	}
	
	//VTD-XML
	@Override
	public void modify(AndroidManifestDocument doc) {
		Multimap<String, String> addedPermissions = action.getAddedPermissions();
		Collection<String> permissions = addedPermissions.get(doc.getManifestId());

		for (String permission : permissions) {
			doc.addPermission(permission);
		}
	}
}
