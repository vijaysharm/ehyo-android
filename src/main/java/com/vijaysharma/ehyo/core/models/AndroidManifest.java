package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Set;

public class AndroidManifest implements HasDocument {
	private final File file;
	private final String id;
	private final SourceSetType type;
	private final Set<String> permissions;
	private final String project;
	private final String packageName;
	private final File sourceDirectory;
	private final File resourceDirectory;
	
	public AndroidManifest(File file, 
						   String projectName,
						   String packageName,
						   SourceSetType sourceSet,
						   File sourceDirectory,
						   File resourceDirectory,
						   Set<String> permissions) {
		this.file = file;
		this.project = projectName;
		this.packageName = packageName;
		this.type = sourceSet;
		this.sourceDirectory = sourceDirectory;
		this.resourceDirectory = resourceDirectory;
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
	
	public String getPackageName() {
		return packageName;
	}
	
	@Override
	public File getFile() {
		return file;
	}

	public Set<String> getPermissions() {
		return this.permissions;
	}
	
	public File getSourceDirectory() {
		return sourceDirectory;
	}
	
	public File getResourceDirectory() {
		return resourceDirectory;
	}

	@Override
	public String toString() {
		return getProject() + ":" + getSourceSet().getType() + ":" + getFile().getName();
	}
}
