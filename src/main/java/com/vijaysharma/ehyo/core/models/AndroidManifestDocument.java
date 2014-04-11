package com.vijaysharma.ehyo.core.models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class AndroidManifestDocument implements AsListOfStrings {
	public static AndroidManifestDocument read(File file, String id) {
		try {
			SAXBuilder builder = new SAXBuilder();
			return new AndroidManifestDocument(id, builder.build(file));
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}		
	}

	private final Document document;
	private final String manifestId;
	
	public AndroidManifestDocument(String id, Document document) {
		this.document = document;
		this.manifestId = id;
	}

	@Override
	public List<String> toListOfStrings() {
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			xmlOutput.output(document, stream);
	
			return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()));
		} catch ( IOException ioe ) {
			throw new UncheckedIoException(ioe);
		}
	}

	public void addPermission(Element usesPermission) {
		document.getRootElement().addContent(0, usesPermission);
	}
}
