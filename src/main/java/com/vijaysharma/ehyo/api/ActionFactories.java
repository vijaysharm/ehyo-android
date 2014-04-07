package com.vijaysharma.ehyo.api;

public interface ActionFactories {
	public interface ManifestActionFactory {
		ManifestAction create();
	}
	
	public interface BuildActionFactory {
		BuildAction create();
	}
}
