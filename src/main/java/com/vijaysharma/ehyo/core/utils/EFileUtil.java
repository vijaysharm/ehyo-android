package com.vijaysharma.ehyo.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.vijaysharma.ehyo.core.models.AndroidManifest;

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

	public static String getCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}

	public static void write(AndroidManifest manifest, List<String> lines) {
		try {
			FileUtils.writeLines(manifest.getFile(), lines);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
}
