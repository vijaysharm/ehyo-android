package com.vijaysharma.ehyo.core.templates;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.ActivityToLayout;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.CamelCaseToUnderscore;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.ClassToResource;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.EscapeXmlAttribute;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.EscapeXmlString;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.ExtractLetters;
import com.vijaysharma.ehyo.core.templates.TemplateMethods.SlashedPackageName;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class TemplatePlayer {
	public static void main(String[] args) {
		String[] templates = {
//			"/templates/activities/MapFragmentMasterDetail",
//			"/templates/activities/SherlockBlankActivity",
//			"/templates/activities/SherlockMasterDetailFlow",
//			"/templates/activities/SlidingPaneMasterDetailFlow",
//			"/templates/activities/TVLeftNavBarActivity",

			"/templates/activities/BlankActivity",
//			"/templates/activities/EmptyActivity",
//			"/templates/activities/FullscreenActivity",
//			"/templates/activities/GoogleMapsActivity", // Does not work "projectOut"
//			"/templates/activities/GooglePlayServicesActivity",
//			"/templates/activities/LoginActivity",
//			"/templates/activities/MasterDetailFlow", // Does not work "minApiLevel"
//			"/templates/activities/SettingsActivity",
			
//			"/templates/other/AppWidget",
//			"/templates/other/BlankFragment",
//			"/templates/other/BroadcastReceiver",
//			"/templates/other/ContentProvider",
//			"/templates/other/CustomView",	// Doesn't work isLibraryProject && isGradle
//			"/templates/other/Daydream",
//			"/templates/other/IntentService",
//			"/templates/other/ListFragment",
//			"/templates/other/Notification",
//			"/templates/other/PlusOneFragment",
//			"/templates/other/Service",
			
//			"/templates/other/EfficientListAdapter",
//			"/templates/other/ParcelableType"
		};

		String templateRootDirectory = templates[0];
		
		final Configuration config = config( TemplatePlayer.class, templateRootDirectory );
		URL templateRoot = TemplatePlayer.class.getResource(templateRootDirectory);
		File templateFileRoot = new File(templateRoot.getFile());
		
		File templateStartingPoint = new File(templateFileRoot, "template.xml");
		Document templateDocument = load(templateStartingPoint);
		
		AndroidTemplateRootModel model = new AndroidTemplateRootModel(templateDocument);
		final Map<String, Object> mapping = Maps.newHashMap();
		
		for ( TemplateParameters param : model.getParameters() ) {
			mapping.put(param.getId(), param.getDefaultValue());
		}
		
		// TODO: This might be acceptable until ehyo supports creating new projects
		mapping.put("isNewProject", false);
		mapping.put("buildApi", 16);
		mapping.put("minApiLevel", 16);
		
		// TODO: These things need to be defined up-front
		mapping.put("manifestDir", "/to/manifest/dir");
		mapping.put("srcDir", "/to/src/dir");
		mapping.put("resDir", "/to/res/dir");
		mapping.put("packageName", "com.vijaysharma.test");
		mapping.put("slashedPackageName", new SlashedPackageName());
		mapping.put("escapeXmlAttribute", new EscapeXmlAttribute());
		mapping.put("classToResource", new ClassToResource());
		mapping.put("camelCaseToUnderscore", new CamelCaseToUnderscore());
		mapping.put("escapeXmlString", new EscapeXmlString());
		mapping.put("extractLetters", new ExtractLetters());
		mapping.put("activityToLayout", new ActivityToLayout());

		Template globalTemplate = get(config, model.getGlobalsFilename());
		Document globalDocument = asDocument(globalTemplate, mapping);
		GlobalDocumentModel globalModel = new GlobalDocumentModel(globalDocument);
		
		globalModel.populate(mapping);
		// TODO: These variables aren't always defined in the global, 
		//		 There needs to be a default value associated
		mapping.put("manifestOut", globalModel.getValue("manifestOut"));
		
		Template recipeTemplate = get(config, model.getRecipeFilename());
		Document recipeDocument = asDocument(recipeTemplate, mapping);
		RecipeDocumentModel recipeModel = new RecipeDocumentModel(recipeDocument, mapping);
		recipeModel.read( new RecipeDocumentCallback() {
			@Override
			public void onInstantiate(String from, String to) {
				Template template = get(config, from);
				List<String> result = asListOfStrings(template, mapping);
				
				System.out.println("instantiate");
				System.out.println(result);
				System.out.println("to: " + to + "\n");
			}

			@Override
			public void onMerge(String from, String to) {
				Template template = get(config, from);
				List<String> result = asListOfStrings(template, mapping);
				
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
	
	private static void dump(Template template, Map<String, Object> mapping) {
		Writer out = new OutputStreamWriter(System.out);
		try {
			template.process(mapping, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> asListOfStrings(Template template, Map<String, Object> mapping) {
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
	
	private static Document asDocument(Template template, Map<String, Object> mapping) {
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

	private static Template get(Configuration config, String path) {
		try {
			return config.getTemplate(path);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
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
		private final Map<String, Object> mapping;

		public RecipeDocumentModel(Document document, Map<String, Object> mapping) {
			this.document = document;
			this.mapping = mapping;
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
	
	private static class GlobalDocumentModel {
		private final Document document;

		public GlobalDocumentModel(Document document) {
			this.document = document;
		}
		
		public String getValue(String key) {
			Element root = document.getRootElement();
			
			for ( Element element : root.getChildren("global") ) {
				if (key.equals(element.getAttributeValue("id")))
					return element.getAttributeValue("value");
			}
			
			return null;
		}
		
		public void populate( Map<String, Object> mapping ) {
			Element root = document.getRootElement();
			
			for ( Element element : root.getChildren("global") ) {
//				if (key.equals(element.getAttributeValue("id")))
//					return element.getAttributeValue("value");
				mapping.put(element.getAttributeValue("id"),
							element.getAttributeValue("value"));
			}
		}
	}
	
	private static class AndroidTemplateRootModel {
		private final Document document;

		public AndroidTemplateRootModel(Document document) {
			this.document = document;
		}

		public String getCategory() {
			Element root = document.getRootElement();
			
			// get the category
			Element categoryElement = root.getChild("category");
			return categoryElement.getAttributeValue("value");
		}
		
		public List<TemplateParameters> getParameters() {
			List<TemplateParameters> parameters = Lists.newArrayList();
			Element root = document.getRootElement();
			for ( Element element : root.getChildren("parameter") ) {
				String id = element.getAttributeValue("id");
				String name = element.getAttributeValue("name");
				String type = element.getAttributeValue("type");
				String defaultValue = element.getAttributeValue("default");
				
				parameters.add(new TemplateParameters(id, type, defaultValue));
			}
			
			return parameters;
		}
		
		public String getGlobalsFilename() {
			Element root = document.getRootElement();
			Element element = root.getChild("globals");
			return element.getAttributeValue("file");
		}
		
		public String getRecipeFilename() {
			Element root = document.getRootElement();
			Element element = root.getChild("execute");
			return element.getAttributeValue("file");
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
}
