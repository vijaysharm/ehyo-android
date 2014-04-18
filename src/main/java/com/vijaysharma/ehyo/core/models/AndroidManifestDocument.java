package com.vijaysharma.ehyo.core.models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class AndroidManifestDocument implements AsListOfStrings {
	public static AndroidManifestDocument read(File file) {
		try {
			SAXBuilder builder = new SAXBuilder();
			return new AndroidManifestDocument(builder.build(file), file);
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}		
	}

	private static final Namespace ANDROID_NAMESPACE = Namespace.getNamespace("android", "http://schemas.android.com/apk/res/android");
	private final Document document;
	private final File file;
	
	public AndroidManifestDocument(Document document, File file) {
		this.document = document;
		this.file = file;
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

	public String getPackage() {
		return document.getRootElement().getAttributeValue("package");
	}
	
	public Set<String> getPermissions() {
		ImmutableSet.Builder<String> permissions = ImmutableSet.builder();
		Element root = document.getRootElement();
		for (Element target : root.getChildren("uses-permission")) {
			permissions.add(target.getAttributeValue("name", ANDROID_NAMESPACE));
		}
		
		return permissions.build();
	}
	
	public List<String> getActivities() {
		ImmutableList.Builder<String> activities = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		for (Element target : application.getChildren("activity")) {
			activities.add(target.getAttributeValue("name", ANDROID_NAMESPACE));
		}
		
		return activities.build();
	}
	
	public List<String> getServices() {
		ImmutableList.Builder<String> activities = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		for (Element target : application.getChildren("service")) {
			activities.add(target.getAttributeValue("name", ANDROID_NAMESPACE));
		}
		
		return activities.build();
	}
	
	public void addPermission(Set<String> permissions) {
		for ( String permission : permissions ) {
			Element usesPermission = new Element("uses-permission")
			.setAttribute("name", permission, ANDROID_NAMESPACE);

			document.getRootElement().addContent(0, usesPermission);			
		}
	}

	public void removePermission(Set<String> permission) {
		Element root = document.getRootElement();
		for (Element target : root.getChildren("uses-permission")) {
			if ( permission.contains(target.getAttributeValue("name", ANDROID_NAMESPACE)) ) {
				root.removeContent(target);
			}
		}	
	}

	public AndroidManifestDocument copy() {
		return AndroidManifestDocument.read(this.file);
	}
}
