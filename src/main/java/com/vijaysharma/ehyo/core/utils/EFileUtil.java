package com.vijaysharma.ehyo.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class EFileUtil {
	private static final String UTF_8 = "UTF-8";

	public static LineIterator lineIterator( File file ) {
		try {
			return FileUtils.lineIterator(file, UTF_8);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	public static List<String> readLines( File file ) {
		try {
			return FileUtils.readLines(file, UTF_8);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
}