package com.vijaysharma.ehyo.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.core.TemplateMethods.ActivityToLayout;
import com.vijaysharma.ehyo.core.TemplateMethods.CamelCaseToUnderscore;
import com.vijaysharma.ehyo.core.TemplateMethods.ClassToResource;
import com.vijaysharma.ehyo.core.TemplateMethods.EscapeXmlAttribute;
import com.vijaysharma.ehyo.core.TemplateMethods.EscapeXmlString;
import com.vijaysharma.ehyo.core.TemplateMethods.ExtractLetters;
import com.vijaysharma.ehyo.core.TemplateMethods.SlashedPackageName;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

class DefaultTemplate implements Template {
	private final String path;
	private final Document templateDocument;

	// TODO: This should probably be moved to a factory
	public DefaultTemplate(String path) {
		this.path = path;
		URL templateRoot = DefaultTemplate.class.getResource(path);
		File templateFileRoot = new File(templateRoot.getFile());
		File templateStartingPoint = new File(templateFileRoot, "template.xml");
		this.templateDocument = load(templateStartingPoint);		
	}
	
	/**
	 * TODO: The template also needs parameters applied to it (mostly for
	 * suggestion values).
	 */
	@Override
	public List<TemplateParameters> loadTemplateParameters() {
		return getParameters(templateDocument);
	}

	public void getActions(Map<String, Object> properties) {
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
		final Configuration config = config( DefaultTemplate.class, path );
		freemarker.template.Template globalTemplate = get(config, model.getAttribute("globals", "file"));
		Document globalDocument = asDocument(globalTemplate, mapping.build());
		DocumentHelper globalModel = new DocumentHelper(globalDocument);
		
//		globalModel.populate(mapping);
		for ( Element element : globalDocument.getRootElement().getChildren("global") ) {
			mapping.put(element.getAttributeValue("id"),
						element.getAttributeValue("value"));
		}
		
		// TODO: This variable isn't always defined in the global, 
		//		 There needs to be a default value associated
		if ( !mapping.build().containsKey("manifestOut") ) {
			mapping.put("manifestOut", globalModel.getValue("global", "manifestOut"));
		}
		
		freemarker.template.Template recipeTemplate = get(config, model.getAttribute("execute", "file"));
		Document recipeDocument = asDocument(recipeTemplate, mapping.build());
		RecipeDocumentModel recipeModel = new RecipeDocumentModel(recipeDocument);

		recipeModel.read( new RecipeDocumentCallback() {
			@Override
			public void onInstantiate(String from, String to) {
				freemarker.template.Template template = get(config, from);
				List<String> result = asListOfStrings(template, mapping.build());
				
				System.out.println("instantiate");
				System.out.println(result);
				System.out.println("to: " + to + "\n");
			}

			@Override
			public void onMerge(String from, String to) {
				freemarker.template.Template template = get(config, from);
				List<String> result = asListOfStrings(template, mapping.build());
				
				System.out.println("merge");
				System.out.println(result);				
				System.out.println("to: " + to + "\n");
			}

			@Override
			public void onCopy(String from, String to) {
				System.out.println("copy\n" + from + " to " + to + "\n");
			}

			@Override
			public void onDependency(String dependency) {
				System.out.println("dependency\n" + dependency + "\n");
			}
		});		
	}

	private static void dump(freemarker.template.Template template, Map<String, Object> mapping) {
		Writer out = new OutputStreamWriter(System.out);
		try {
			template.process(mapping, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> asListOfStrings(freemarker.template.Template template, Map<String, Object> mapping) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			template.process(mapping, new OutputStreamWriter(output));
			return IOUtils.readLines(new ByteArrayInputStream(output.toByteArray()));
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
	
	private static Document asDocument(freemarker.template.Template template, Map<String, Object> mapping) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			template.process(mapping, new OutputStreamWriter(output));
			return new SAXBuilder().build(new ByteArrayInputStream(output.toByteArray()));
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static interface RecipeDocumentCallback {
		void onInstantiate(String from, String to);
		void onMerge(String from, String to);
		void onCopy(String from, String to);
		void onDependency(String dependency);
	}
	
	private static class RecipeDocumentModel {
		private final Document document;

		public RecipeDocumentModel(Document document) {
			this.document = document;
		}
		
		public void read( RecipeDocumentCallback callback ) {
			Element root = document.getRootElement();
			
			// TODO: Should add root as part of path
			for ( Element child : root.getChildren() ) {
				if ( "merge".equals( child.getName() ) ) {
					String from = fromPath(child.getAttributeValue("from"));
					String to = child.getAttributeValue("to");
					callback.onMerge( from, to );
				} else if ( "instantiate".equals( child.getName() ) ) {
					String from = fromPath(child.getAttributeValue("from"));
					String to = child.getAttributeValue("to");
					callback.onInstantiate( from, to );
				} else if ( "dependency".equals( child.getName() ) ) {
					String dependency = child.getAttributeValue("mavenUrl");
					callback.onDependency(dependency);
				} else if ( "copy".equals( child.getName() ) ) {
					String from = fromPath(child.getAttributeValue("from"));
					String to = child.getAttributeValue("to");
					callback.onCopy( from, to );					
				} 
			}
		}

		private String fromPath(String path) {
			// TODO: This isn't right
			if ( path == null )
				return null;

			String separator = path.startsWith("/") ? "" : File.separator;
			return Joiner.on(separator).join("root", path);
		}
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
			String constraints = element.getAttributeValue("constraints");
			String help = element.getAttributeValue("help");
			
			// TODO: Suggest may need to be interpolated (its a template value);
//			String suggest = element.getAttributeValue("suggest");
			
			parameters.add(new TemplateParameters(id, name, type, defaultValue, constraints, help));
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

	private static freemarker.template.Template get(Configuration config, String path) {
		try {
			return config.getTemplate(path);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
	
	private static Configuration config( Class<?> clazz, String name ) {
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(clazz, name);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
		
		return cfg;
	}
}
