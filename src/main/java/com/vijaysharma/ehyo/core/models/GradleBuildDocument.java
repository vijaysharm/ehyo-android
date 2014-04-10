package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;

import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

public class GradleBuildDocument {
	public static GradleBuildDocument read(File file, String id) {
		return new GradleBuildDocument(EFileUtil.readLines(file), id);
	}
	
	private final List<String> lines;
	private final String gradleId;
	
	private GradleBuildDocument(List<String> lines, String gradleId) {
		this.lines = lines;
		this.gradleId = gradleId;
	}
	
	public String getGradleId() {
		return gradleId;
	}
	
	public List<String> asListOfStrings() {
		return lines;
	}

	public GradleBuildDocument appendDependency(String dependency) {
		Outputter.out.print("Adding " + dependency);
		return this;
	}
}
