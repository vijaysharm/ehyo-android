package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.TemplateFileParameter;
import com.vijaysharma.ehyo.api.TemplateInfo;
import com.vijaysharma.ehyo.core.RecipeDocumentModel.RecipeDocumentCallback;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultProjectSourceSet;
import com.vijaysharma.ehyo.core.TemplateMethods.ActivityToLayout;
import com.vijaysharma.ehyo.core.TemplateMethods.CamelCaseToUnderscore;
import com.vijaysharma.ehyo.core.TemplateMethods.ClassToResource;
import com.vijaysharma.ehyo.core.TemplateMethods.EscapeXmlAttribute;
import com.vijaysharma.ehyo.core.TemplateMethods.EscapeXmlString;
import com.vijaysharma.ehyo.core.TemplateMethods.ExtractLetters;
import com.vijaysharma.ehyo.core.TemplateMethods.SlashedPackageName;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;

class DefaultTemplate implements InternalTemplate {
	private final Document templateDocument;
	private final TemplateConverter converter;
	private final File templateFileRoot;
	private final TemplateConfigLoader configLoader;
	
	// TODO: This should probably be moved to a factory
	public DefaultTemplate(TemplateConfigLoader configLoader, File templateRoot) {
		this.configLoader = configLoader;
		this.templateFileRoot = templateRoot; 
		this.templateDocument = load(new File(templateFileRoot, "template.xml"));
		this.converter = new TemplateConverter();
	}
	
	@Override
	public DefaultTemplateInfo loadTemplateInformation() {
		final Element root = templateDocument.getRootElement();
		return new DefaultTemplateInfo(root);
	}
	
	@Override
	public List<TemplateFileParameter> loadTemplateParameters(ProjectSourceSet sourceSet) {
		DefaultProjectSourceSet ss = (DefaultProjectSourceSet) sourceSet;
		return getParameters(templateDocument, ss);
	}

	@Override
	public void apply(Map<String, Object> properties, RecipeDocumentCallback callback) {
		final ImmutableMap.Builder<String, Object> mapping = ImmutableMap.builder();
		
		mapping.putAll(properties); 
		mapping.put("slashedPackageName", new SlashedPackageName());
		mapping.put("escapeXmlAttribute", new EscapeXmlAttribute());
		mapping.put("classToResource", new ClassToResource());
		mapping.put("camelCaseToUnderscore", new CamelCaseToUnderscore());
		mapping.put("escapeXmlString", new EscapeXmlString());
		mapping.put("extractLetters", new ExtractLetters());
		mapping.put("activityToLayout", new ActivityToLayout());
		
		DocumentHelper model = new DocumentHelper(templateDocument);
		final Configuration config = configLoader.create();
		freemarker.template.Template globalTemplate = converter.get(config, model.getAttribute("globals", "file"));
		Document globalDocument = converter.asDocument(globalTemplate, mapping.build());
		
		for ( Element element : globalDocument.getRootElement().getChildren("global") ) {
			String id = element.getAttributeValue("id");
			String value = element.getAttributeValue("value");

			if (mapping.build().containsKey(id))
				continue;

			mapping.put(id, value);
		}
		
		freemarker.template.Template recipeTemplate = converter.get(config, model.getAttribute("execute", "file"));
		Document recipeDocument = converter.asDocument(recipeTemplate, mapping.build());
		RecipeDocumentModel recipeModel = new RecipeDocumentModel(templateFileRoot, recipeDocument, config, mapping.build(), converter);

		recipeModel.read( callback );
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
	
	public static List<TemplateFileParameter> getParameters(Document document, DefaultProjectSourceSet sourceSet) {
		List<TemplateFileParameter> parameters = Lists.newArrayList();
		Element root = document.getRootElement();
		for ( Element element : root.getChildren("parameter") ) {
			String id = element.getAttributeValue("id");
			String name = element.getAttributeValue("name");
			String type = element.getAttributeValue("type");
			String defaultValue = element.getAttributeValue("default");
			String constraints = element.getAttributeValue("constraints");
			String help = element.getAttributeValue("help");
			
			// TODO: Suggest may need to be interpolated (its a template value);
			// String suggest = element.getAttributeValue("suggest");
			
			ImmutableMap.Builder<String, String> options = ImmutableMap.builder();
			for ( Element option : element.getChildren("option") ) {
				options.put(option.getAttributeValue("id"), option.getTextTrim());
			}
			
			// TODO: Yuck... 
			if ( "packagename".equalsIgnoreCase(id) ) {
				defaultValue = sourceSet.getSourceSet().getManifest().getPackageName(); 
			}
			
			parameters.add(new DefaultTemplateFileParameter(id, name, type, defaultValue, constraints, help, options.build()));
		}
		
		return parameters;
	}
	
	private static class DocumentHelper {
		private final Document document;
		
		public DocumentHelper(Document document) {
			this.document = document;
		}
		
		public String getAttribute(String section, String attribute) {
			Element root = document.getRootElement();
			Element element = root.getChild(section);
			return element.getAttributeValue(attribute);
		}
	}

//	private static Configuration config( Class<?> clazz, String name ) {
//		Configuration cfg = new Configuration();
//		cfg.setClassForTemplateLoading(clazz, name);
//		cfg.setObjectWrapper(new DefaultObjectWrapper());
//		cfg.setDefaultEncoding("UTF-8");
//		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
//		
//		return cfg;
//	}
	
	private static class DefaultTemplateInfo implements TemplateInfo {
		private final Element root;
		public DefaultTemplateInfo(Element root) {
			this.root = root;
		}
		
		@Override
		public String getName() {
			return root.getAttributeValue("name");
		}
		
		@Override
		public String getDescription() {
			return root.getAttributeValue("description");
		}
	}
	
	private static class DefaultTemplateFileParameter implements TemplateFileParameter {
		private final String id;
		private final String defaultValue;
		private final String type;
		private final String name;
		private final String help;
		private final Map<String, String> options;

		public DefaultTemplateFileParameter(String id, 
										    String name,
										    String type,
										    String defaultValue,
										    String constraints,
										    String help, 
										    Map<String,String> options) {
			this.id = id;
			this.type = type;
			this.defaultValue = defaultValue;
			this.name = name;
			this.help = help;
			this.options = options;
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getType() {
			return type;
		}
		
		@Override
		public String getDefaultValue() {
			return defaultValue;
		}
		
		@Override
		public String getHelp() {
			return help;
		}
		
		@Override
		public Map<String, String> getOptions() {
			return options;
		}
	}
}
