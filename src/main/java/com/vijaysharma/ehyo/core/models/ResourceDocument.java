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

public class ResourceDocument implements AsListOfStrings {
	public static ResourceDocument read( File file ) {
		try {
			SAXBuilder builder = new SAXBuilder();
			
			if ( file.exists() ) {
				return new ResourceDocument(builder.build(file));
			} else {
				StringBuilder empty = new StringBuilder();
				empty.append("<resources>");
				empty.append("</resources>");
				ByteArrayInputStream inputStream = new ByteArrayInputStream(empty.toString().getBytes());
				
				return new ResourceDocument(builder.build(inputStream));
			}
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}
	}

	private final Document resource;
	public ResourceDocument(Document resource) {
		this.resource = resource;
	}
	
	public void add(ResourceDocument toMerge) {
		Element root = resource.getRootElement();
		for( Element element : toMerge.resource.getRootElement().getChildren() ) {
			root.addContent(element.clone());
		}
	}
	
	@Override
	public List<String> toListOfStrings() {
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			xmlOutput.output(resource, stream);
	
			return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()));
		} catch ( IOException ioe ) {
			throw new UncheckedIoException(ioe);
		}
	}
}
