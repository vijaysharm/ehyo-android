package com.vijaysharma.ehyo.api;

public interface ManifestAction extends PluginAction {
	void addPermission(String permission);
	void removePermission(String permission);
}
