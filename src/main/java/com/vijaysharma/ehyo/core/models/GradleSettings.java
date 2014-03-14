package com.vijaysharma.ehyo.core.models;

import java.io.File;

public class GradleSettings {
	public static GradleSettings read(String directory) {
		File root = new File(directory);
		
		return new GradleSettings();
	}

}
