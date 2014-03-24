package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

public class GradleSettings {
	public static GradleSettings read(File file) {
		ImmutableList.Builder<String> projects = ImmutableList.builder();
		List<String> lines = EFileUtil.readLines(file);
		
		for ( String line: lines )
			projects.addAll(readProjectRoot(line));

		return new GradleSettings(projects.build());
	}
	
	private static List<String> readProjectRoot( String line ) {
		List<String> projects = Lists.newArrayList();
		String include = "include";

		if ( ! line.startsWith("include") )
			return projects;
		
		String projectList = line.substring(include.length() + 1);
		String[] projectLists = projectList.split(",");
		for ( String project : projectLists ) {
			String prep = project.trim().replaceAll("'", "");
			if ( prep.startsWith(":"))
				projects.add(prep.substring(1));
			else
				projects.add(prep.replaceAll(":", "/"));
		}
		
		return projects;
	}

	private final List<String> projects;
	
	private GradleSettings(List<String> projects) {
		this.projects = projects;
	}

	public List<String> getProjects() {
		return projects;
	}
}
