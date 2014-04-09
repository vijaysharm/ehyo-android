package com.vijaysharma.ehyo.core.models;

import java.io.File;

public class GradleBuild {
	public static GradleBuild read(File file) {
		return new GradleBuild(file);
	}
	
	private final File file;
	
	public GradleBuild(File file) {
		this.file = file;
	}

	public String getProject() {
		return file.getParentFile().getName();
	}
}
