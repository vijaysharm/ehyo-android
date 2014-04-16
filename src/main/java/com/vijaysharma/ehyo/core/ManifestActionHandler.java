package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestActionHandler implements PluginActionHandler<AndroidManifestDocument, ManifestActions> {
	
	//VTD-XML
	@Override
	public void modify(AndroidManifestDocument doc, ManifestActions action) {
		Collection<String> permissions = action.getAddedPermissions();

		for (String permission : permissions) {
			doc.addPermission(permission);
		}
	}
}
