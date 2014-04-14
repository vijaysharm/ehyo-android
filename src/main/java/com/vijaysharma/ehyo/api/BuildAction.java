package com.vijaysharma.ehyo.api;

public interface BuildAction extends PluginAction {
	void addDependency(BuildConfiguration variant, String dependency);
	void removeDependency(BuildConfiguration config, String dependency);
}
