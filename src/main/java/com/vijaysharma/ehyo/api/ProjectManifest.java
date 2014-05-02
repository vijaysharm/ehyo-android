package com.vijaysharma.ehyo.api;

import java.util.Set;

public interface ProjectManifest {
	void addPermission(String permission);
	void addPermissions(Set<String> permissions);
	void removePermission(String permission);
	void removePermissions(Set<String> permissions);
	Set<String> getPermissions();
}
