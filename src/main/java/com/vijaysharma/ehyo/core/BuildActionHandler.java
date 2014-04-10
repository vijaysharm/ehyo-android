package com.vijaysharma.ehyo.core;

import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.core.InternalActions.InternalBuildAction;

public class BuildActionHandler implements PluginActionHandler<BuildConfiguration>{
	private InternalBuildAction action;

	public BuildActionHandler(InternalBuildAction action) {
		this.action = action;
	}

	@Override
	public void modify(BuildConfiguration item) {
		
	}
}
