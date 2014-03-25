package com.vijaysharma.ehyo.api.plugins;

import com.vijaysharma.ehyo.api.Plugin;

public class ListPlugins implements Plugin {
	
	@Override
	public String name() {
		return ListPlugins.class.getSimpleName();
	}
}
