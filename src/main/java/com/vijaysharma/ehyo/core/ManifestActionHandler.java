package com.vijaysharma.ehyo.core;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;

public class ManifestActionHandler implements PluginActionHandler<Document> {
	private static final Namespace ANDROID_NAMESPACE = Namespace.getNamespace("android", "http://schemas.android.com/apk/res/android");
	private final InternalManifestAction action;

	public ManifestActionHandler(InternalManifestAction action) {
		this.action = action;
	}
	
	//VTD-XML
	@Override
	public void modify(Document doc) {
		List<String> permissions = action.getAddedPermissions();
		for (String permission : permissions) {
			Element usesPermission = new Element("uses-permission")
				.setAttribute("name", permission, ANDROID_NAMESPACE);

			doc.getRootElement().addContent(0, usesPermission);
		}
		
		permissions = action.getRemovedPermissions();
		for (String permission : permissions) {
			Element usesPermission = new Element("uses-permission")
				.setAttribute("name", permission, ANDROID_NAMESPACE);

//			doc.getRootElement().addContent(0, usesPermission);
		}
	}
}
