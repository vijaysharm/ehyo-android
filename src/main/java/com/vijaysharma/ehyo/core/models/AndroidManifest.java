package com.vijaysharma.ehyo.core.models;

import java.io.File;

public class AndroidManifest implements HasDocument {
	public static AndroidManifest read(File file) {
		return new AndroidManifest(file);
	}

	private final File file;
	private final String id;
	
	public AndroidManifest(File file) {
		this.file = file;
		this.id = file.getAbsolutePath();
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
	
	public File getFile() {
		return file;
	}

	@Override
	public AndroidManifestDocument asDocument() {
		return AndroidManifestDocument.read(this.file, this.id);
	}
}
