package com.vijaysharma.ehyo.core;

import java.util.List;

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
//		List<String> permissions = action.getAddedPermissions();
//		for (String permission : permissions) {
//			doc.addPermission(permission);
//		}
//		
//		permissions = action.getRemovedPermissions();
//		for (String permission : permissions) {
//			doc.removePermission(permission);
//		}
	}
}
