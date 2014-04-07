package com.vijaysharma.ehyo.core;

import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;

public class PluginActionHandlerFactory {

	public PluginActionHandler<?> create(PluginAction action) {
		if ( action instanceof InternalManifestAction )
			return new ManifestActionHandler((InternalManifestAction) action);
		
		return null;
	}

}
