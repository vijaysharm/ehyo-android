package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestActionHandler implements PluginActionHandler<AndroidManifestDocument, ManifestActions> {
	
	//VTD-XML
	@Override
	public void modify(AndroidManifestDocument doc, ManifestActions action) {
		Multimap<String, String> addedPermissions = action.getAddedPermissions();
		Collection<String> permissions = addedPermissions.get(doc.getManifestId());

		for (String permission : permissions) {
			doc.addPermission(permission);
		}
	}
}
