package com.vijaysharma.ehyo.core;

public class PluginActionHandlerFactory {
	public ManifestActionHandler createManifestActionHandler() {
		return new ManifestActionHandler();
	}

	public BuildActionHandler createBuildActionHandler() {
		return new BuildActionHandler();
	}
}
