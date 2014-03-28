package com.vijaysharma.ehyo.core;

import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.ManifestActionFactory;

class DefaultManifestActionFactory implements ManifestActionFactory {
	@Override
	public ManifestAction create() {
		return new InternalManifestAction();
	}
}
