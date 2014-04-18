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
		Collection<String> add = action.getAddedPermissions();
		Collection<String> remove = action.getRemovedPermissions();
		Collection<String> all = doc.getPermissions();
		
		Set<String> toBeAdded = Sets.newHashSet(add);
		Set<String> toBeRemoved = Sets.newHashSet(remove);
		toBeAdded.removeAll(remove);
		toBeRemoved.removeAll(add);
		
		for ( String permission : remove ) {
			if (!all.contains(permission))
				toBeRemoved.remove(permission);
		}
		
		toBeAdded.removeAll(all);
		
		if ( ! toBeAdded.isEmpty() )
			doc.addPermission(toBeAdded);
		
		if ( !toBeRemoved.isEmpty() )
			doc.removePermission(toBeRemoved);
		
//		for ( String permission : remove ) {
//			if ( all.contains(permission) ) {
//				doc.removePermission(permission);
//			} else {
//				out.println( permission + " does not exist in manifest" );
//			}
//			
//			add.remove(permission);
//		}
//		
//		for ( String permission : all ) {
//			if ( add.contains(permission) ) {
//				out.println( permission + " already exists in manifest" );
//				add.remove(permission);
//			}
//		}
//		
//		for (String permission : add) {
//			doc.addPermission(permission);
//		}
	}
}
