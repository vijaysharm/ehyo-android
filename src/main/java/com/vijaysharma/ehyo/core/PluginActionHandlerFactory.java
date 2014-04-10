package com.vijaysharma.ehyo.core;

import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;

public class PluginActionHandlerFactory {
	public ManifestActionHandler createManifestActionHandler(InternalManifestAction action) {
		return new ManifestActionHandler(action);
	}

	public BuildActionHandler createBuildActionHandler(InternalBuildAction action) {
		return new BuildActionHandler(action);
	}
}
