package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Set;

public class AndroidManifest implements HasDocument {
	private final File file;
	private final String id;
	private final SourceSetType type;
	private final Set<String> permissions;
	private final String project;
	
	public AndroidManifest(File file, String projectName, SourceSetType sourceSet, Set<String> permissions) {
		this.file = file;
		this.project = projectName;
		this.type = sourceSet;
		this.permissions = permissions;
		this.id = file.getAbsolutePath();
	}

	public String getProject() {
		return project;
	}
	
	public SourceSetType getSourceSet() {
		return type;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public File getFile() {
		return file;
	}

	public Set<String> getPermissions() {
		return this.permissions;
	}
	
	@Override
	public String toString() {
		return getProject() + ":" + getSourceSet().getType() + ":" + getFile().getName();
	}
}
