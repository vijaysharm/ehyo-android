package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.vijaysharma.ehyo.core.utils.EFileUtil;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class AndroidManifest {
	public static AndroidManifest read(File file) {
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

	public String getPath() {
		return EFileUtil.getCanonicalPath(file);
	}
	
//	public File getFile() {
//		return file;
//	}
	
	public Document asXmlDocument() {
		try {
			SAXBuilder builder = new SAXBuilder();
			return builder.build(file);
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}		
	}
}
