package com.vijaysharma.ehyo.api;

public interface ManifestAction extends PluginAction {
	void addPermission(ProjectManifest manifest, String permission);
	void removePermission(ProjectManifest manifest, String permission);
}
