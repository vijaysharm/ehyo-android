package com.vijaysharma.ehyo.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ManifestActionHandler {
	private static final Namespace ANDROID_NAMESPACE = Namespace.getNamespace("android", "http://schemas.android.com/apk/res/android");
	private final InternalManifestAction action;

	public ManifestActionHandler(InternalManifestAction action) {
		this.action = action;
	}

	public void handle(ProjectRegistry registry) {
		// TODO: Ask which project/manifest should be affected
		
		Map<String, Project> projects = registry.getProjects();
		List<AndroidManifest> manifests = Lists.newArrayList();
		for ( Project project : projects.values() ) {
			manifests.addAll(project.getManifests());
		}
		
		read(manifests.get(0).getFile());
	}
	
	//VTD-XML
	private void read(File file) {
		SAXBuilder builder = new SAXBuilder();
		
		Element usesPermission = new Element("uses-permission")
			.setAttribute("name", "android.permission.INTERNET", ANDROID_NAMESPACE);
		
		try {
			Document doc = builder.build(file);
			
			List<String> actual = toListOfStrings(doc);
			doc.getRootElement().addContent(0, usesPermission);			
			List<String> modifiedLines = toListOfStrings(doc);
			Patch diff = DiffUtils.diff(actual, modifiedLines);
			
			for (Delta delta: diff.getDeltas()) {
				System.out.println(delta);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> toListOfStrings(Document doc) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		xmlOutput.output(doc, stream);
		
		List<String> modifiedLines = IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()));
		return modifiedLines;
	}
}
