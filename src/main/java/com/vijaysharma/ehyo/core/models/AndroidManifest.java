package com.vijaysharma.ehyo.core.models;

import java.io.File;

public class AndroidManifest {
	public static AndroidManifest read(File file) {
		// TODO: Store the file as an XML object so we can reference 
		//		 pieces of the manifest directly
		return new AndroidManifest(file);
	}

	private final File file;
	
	public AndroidManifest(File file) {
		this.file = file;
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

	public File getFile() {
		return file;
	}
}
