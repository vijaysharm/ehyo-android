package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.vijaysharma.ehyo.core.InternalActions.ResourceActions;
import com.vijaysharma.ehyo.core.models.ResourceDocument;

public class ResourceActionHandler implements PluginActionHandler<ResourceDocument, ResourceActions> {
	@Override
	public void modify(ResourceDocument item, ResourceActions actions) {
		modifyResource(item, actions);
	}

	private void modifyResource(ResourceDocument item, ResourceActions actions) {
		Collection<ResourceDocument> allMerges = actions.getMergedResources();
		for ( ResourceDocument toMerge : allMerges ) {
			item.add( toMerge );
		}
	}
}
