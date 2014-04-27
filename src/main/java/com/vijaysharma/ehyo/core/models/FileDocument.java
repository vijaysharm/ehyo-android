package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

public class FileDocument implements AsListOfStrings {
	public static FileDocument read( File input ) {
		List<String> lines = Lists.newArrayList();
		if ( input.exists() ) {
			lines.addAll(EFileUtil.readLines(input));
		}

		return new FileDocument(lines);
	}
	
	private List<String> lines;
	public FileDocument(List<String> lines) {
		this.lines = lines;
	}

	public void addAll(List<String> toAdd) {
		lines.addAll(toAdd);
	}

	@Override
	public List<String> toListOfStrings() {
		return ImmutableList.copyOf(this.lines);
	}
}
