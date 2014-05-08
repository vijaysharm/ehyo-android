package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.ManifestTags.Activity;
import com.vijaysharma.ehyo.core.models.ManifestTags.Receiver;
import com.vijaysharma.ehyo.core.models.ManifestTags.Service;

public class ManifestActionHandler implements PluginActionHandler<AndroidManifestDocument, ManifestActions> {
	
	//VTD-XML
	@Override
	public void modify(AndroidManifestDocument doc, ManifestActions action) {
		modifyPermissions(doc, action);
		modifyApplication(doc, action);
	}

	private void modifyApplication(AndroidManifestDocument doc, ManifestActions action) {
		Collection<Activity> activities = action.getAddedActivities();
		for ( Activity activity : activities ) {
			doc.addActivity( activity );
		}
		
		Collection<Service> services = action.getAddedServices();
		for ( Service service : services ) {
			doc.addService( service );
		}
		
		Collection<Receiver> receivers = action.getAddedReceivers();
		for ( Receiver receiver : receivers ) {
			doc.addReceiver( receiver );
		}
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
	}
}
