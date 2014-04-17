package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestActionHandler implements PluginActionHandler<AndroidManifestDocument, ManifestActions> {
	
	private final TextOutput out;
	
	public ManifestActionHandler() {
		this( Output.out );
	}
	
	ManifestActionHandler(TextOutput out) {
		this.out = out;
	}

	//VTD-XML
	@Override
	public void modify(AndroidManifestDocument doc, ManifestActions action) {
		modifyPermissions(doc, action);
	}

	// TODO: Need to output permissions that are not defined in the document
	// during removal, and need to output when adding an existing permission 
	private void modifyPermissions(AndroidManifestDocument doc, ManifestActions action) {
		Set<String> added = Sets.newHashSet(action.getAddedPermissions());
		Collection<String> permissions = action.getRemovedPermissions();
		Collection<String> all = permissions = doc.getPermissions();
		
		for ( String permission : permissions ) {
			if ( all.contains(permission) ) {
				doc.removePermission(permission);
			} else {
				out.println( permission + " does not exist in manifest" );
			}
			
			added.remove(permission);
		}
		
		all = doc.getPermissions();
		for ( String permission : all ) {
			if ( all.contains(permission) ) {
				out.println( permission + " already exists in manifest" );
				added.remove(permission);
			}
		}
		
		for (String permission : added) {
			doc.addPermission(permission);
		}
	}
}
