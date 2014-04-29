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
import com.vijaysharma.ehyo.core.models.ManifestTags.Activity;
import com.vijaysharma.ehyo.core.models.ManifestTags.Application;
import com.vijaysharma.ehyo.core.models.ManifestTags.MetaData;
import com.vijaysharma.ehyo.core.models.ManifestTags.Receiver;
import com.vijaysharma.ehyo.core.models.ManifestTags.Service;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class AndroidManifestDocument implements AsListOfStrings {
	public static AndroidManifestDocument read(File file) {
		try {
			SAXBuilder builder = new SAXBuilder();
			return new AndroidManifestDocument(builder.build(file));
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}
	}

	private static final Namespace ANDROID_NAMESPACE = Namespace.getNamespace("android", "http://schemas.android.com/apk/res/android");
	private final Document document;
	
	public AndroidManifestDocument(Document document) {
		this.document = document;
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
	
	public Application getApplication() {
		Element root = document.getRootElement();
		Element application = root.getChild("application");

		return Application.read(application, ANDROID_NAMESPACE);
	}
	
	public Set<String> getLibraries() {
		ImmutableSet.Builder<String> useslibrary = ImmutableSet.builder();
		Element root = document.getRootElement();
		for (Element target : root.getChildren("uses-library")) {
			useslibrary.add(target.getAttributeValue("name", ANDROID_NAMESPACE));
		}
		
		return useslibrary.build();
	}
	
	public List<Receiver> getReceivers() {
		ImmutableList.Builder<Receiver> receivers = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		if ( application == null )
			return receivers.build();

		for (Element target : application.getChildren("receiver")) {
			receivers.add(Receiver.read(target, ANDROID_NAMESPACE));
		}
		
		return receivers.build();
	}
	
	public void addReceiver(Receiver receiver) {
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		Element element = Receiver.create(receiver, ANDROID_NAMESPACE);
		application.addContent(element);
	}
	
	public List<Activity> getActivities() {
		ImmutableList.Builder<Activity> activities = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		if ( application == null )
			return activities.build();
		
		for (Element target : application.getChildren("activity")) {
			activities.add(Activity.read(target, ANDROID_NAMESPACE));
		}
		
		return activities.build();
	}
	
	public void addActivity(Activity activity) {
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		Element element = Activity.create(activity, ANDROID_NAMESPACE);
		application.addContent(element);
	}
	
	public List<MetaData> getMetadata() {
		ImmutableList.Builder<MetaData> metadata = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		
		if ( application == null )
			return metadata.build();

		for (Element target : application.getChildren("activity")) {
			metadata.add(MetaData.read(target, ANDROID_NAMESPACE));
		}
		
		return metadata.build();
	}
	
	public List<Service> getServices() {
		ImmutableList.Builder<Service> services = ImmutableList.builder();
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		
		if ( application == null )
			return services.build();

		for (Element target : application.getChildren("service")) {
			services.add(Service.read(target, ANDROID_NAMESPACE));
		}
		
		return services.build();
	}
	
	public void addService(Service service) {
		Element root = document.getRootElement();
		Element application = root.getChild("application");
		Element element = Service.create(service, ANDROID_NAMESPACE);
		application.addContent(element);
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
}
