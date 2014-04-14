package com.vijaysharma.ehyo.api;

import java.util.Set;

public interface ProjectManifest {
	void addPermissions(Set<String> permissions);
	void removePermissions(Set<String> permissions);
}
