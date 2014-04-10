package com.vijaysharma.ehyo.api;

public interface BuildAction extends PluginAction {
	void addDependency(BuildConfiguration variant, String dependency);
}
