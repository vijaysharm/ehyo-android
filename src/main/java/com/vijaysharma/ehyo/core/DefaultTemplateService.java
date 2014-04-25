package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.api.TemplateService;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class DefaultTemplateService implements TemplateService {

	/**
	 * TODO: The template also needs parameters applied to it (mostly for
	 * suggestion values).
	 */
	@Override
	public List<TemplateParameters> loadParameters(String path) {
		URL templateRoot = DefaultTemplateService.class.getResource(path);
		File templateFileRoot = new File(templateRoot.getFile());
		File templateStartingPoint = new File(templateFileRoot, "template.xml");
		Document templateDocument = load(templateStartingPoint);
		
		return getParameters(templateDocument);
	}

	private static Document load( File file ) {
		SAXBuilder builder = new SAXBuilder();

		try {
			return builder.build(file);
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}		
	}
	
	public static List<TemplateParameters> getParameters(Document document) {
		List<TemplateParameters> parameters = Lists.newArrayList();
		Element root = document.getRootElement();
		for ( Element element : root.getChildren("parameter") ) {
			String id = element.getAttributeValue("id");
			String name = element.getAttributeValue("name");
			String type = element.getAttributeValue("type");
			String defaultValue = element.getAttributeValue("default");
			String constraints = "";
			String help = "";
			String suggest = "";
			
			parameters.add(new TemplateParameters(id, name, type, defaultValue));
		}
		
		return parameters;
	}
	
	private static class DocumentHelper {
		private final Document document;
		
		public DocumentHelper(Document document) {
			this.document = document;
		}
		
		public String getValue(String section, String key) {
			Element root = document.getRootElement();
			
			for ( Element element : root.getChildren(section) ) {
				if (key.equals(element.getAttributeValue("id")))
					return element.getAttributeValue("value");
			}
			
			return null;
		}
		
		public String getAttribute(String section, String attribute) {
			Element root = document.getRootElement();
			Element element = root.getChild(section);
			return element.getAttributeValue(attribute);
		}
	}
}
