package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Set;

public class AndroidManifest implements HasDocument {
	private final File file;
	private final String id;
	private final Set<String> permissions;
	private final String packageName;
	
	public AndroidManifest(File file, 
						   String packageName,
						   Set<String> permissions) {
		this.file = file;
		this.packageName = packageName;
		this.permissions = permissions;
		this.id = file.getAbsolutePath();
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
}
