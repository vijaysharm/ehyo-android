package com.vijaysharma.ehyo.core;

import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;

public class PluginActionHandlerFactory {

	public PluginActionHandler<?> create(PluginAction action) {
		if ( action instanceof InternalManifestAction )
			return new ManifestActionHandler((InternalManifestAction) action);
		
		if ( action instanceof InternalBuildAction )
			return new BuildActionHandler((InternalBuildAction) action);
		
		return null;
	}

}
