package com.vijaysharma.ehyo.core;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.api.PluginAction;

class PluginActionMapper {
	private final Map<Class<? extends PluginAction>, ManifestActionHandlerFactory> mapping = Maps.newHashMap();
	
	public PluginActionMapper() {
		mapping.put(InternalManifestAction.class, new ManifestActionHandlerFactory());
	}
	
	public Optional<ManifestActionHandler> get(PluginAction action) {
		ManifestActionHandlerFactory factory = mapping.get(action.getClass());
		if ( factory == null )
			return Optional.absent();
		
		
		return Optional.of(factory.create(action));
	}
}
