package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.Set;

public class AndroidManifest implements HasDocument {
	public static AndroidManifest read(File file) {
		return new AndroidManifest(file);
	}

	private final File file;
	private final String id;
	private final AndroidManifestDocument document;
	
	public AndroidManifest(File file) {
		this.file = file;
		this.id = file.getAbsolutePath();
		this.document = AndroidManifestDocument.read(file);
	}

	public String getProject() {
		return file.getParentFile().getParentFile().getParentFile().getName();
	}
	
	/**
	 * Not really a variant, and not really a flavor, but I can't make that
	 * assumption without reading the build file
	 */
	public String getSourceSet() {
		return file.getParentFile().getName();
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public AndroidManifestDocument asDocument() {
		return document.copy();
	}

	public Set<String> getPermissions() {
		return document.getPermissions();
	}
}
