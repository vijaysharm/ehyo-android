package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import com.google.common.base.Joiner;

import freemarker.template.Configuration;

class RecipeDocumentModel {
	static interface RecipeDocumentCallback {
		void onInstantiate(List<String> result, File to);
		void onResourceMerge(List<String> result, File to);
		void onManifestMerge(Document result, File to);
		void onCopy(File from, File to);
		void onDependency(String dependency);
	}
	
	private final Document document;
	private final Configuration config;
	private final Map<String, Object> properties;
	private final TemplateConverter converter;
	private final File templateFileRoot;
	
	public RecipeDocumentModel(File root,
							   Document document, 
							   Configuration config, 
							   Map<String, Object> properties, 
							   TemplateConverter converter) {
		this.templateFileRoot = root;
		this.document = document;
		this.config = config;
		this.properties = properties;
		this.converter = converter;
	}
	
	public void read( RecipeDocumentCallback callback ) {
		Element root = document.getRootElement();
		
		for ( Element child : root.getChildren() ) {
			if ( "merge".equals( child.getName() ) ) {
				String from = child.getAttributeValue("from");
				String to = child.getAttributeValue("to");
				handleMerge(callback, from, to);
			} else if ( "instantiate".equals( child.getName() ) ) {
				String from = child.getAttributeValue("from");
				String to = child.getAttributeValue("to");
				handleInstantiate(callback, from, to);
			} else if ( "copy".equals( child.getName() ) ) {
				String from = child.getAttributeValue("from");
				String to = child.getAttributeValue("to");
				handleCopy(callback, from, to);
			} else if ( "dependency".equals( child.getName() ) ) {
				String dependency = child.getAttributeValue("mavenUrl");
				callback.onDependency(dependency);
			}
		}
	}

	private void handleCopy(RecipeDocumentCallback callback, String fromPath, String toPath) {
		File to = toPath(toPath, fromPath);
		File from = new File(templateFileRoot, fromPath(fromPath));
		
		if ( ! from.exists() )
			throw new IllegalArgumentException("Cannot copy from " + fromPath + " it does not exist");
		
		if ( from.isDirectory() ) {
			recurse(from.listFiles(), to, callback);
		} else if ( from.isFile() ) {
			doCopyCallback(callback, to, from);
		}
	}

	private void recurse(File[] files, File to, RecipeDocumentCallback callback) {
		for ( File file : files ) {
			if ( file.isDirectory() ) {
				recurse(file.listFiles(), new File(to, file.getName()), callback);
			}
			
			if ( file.isFile() ) {
				doCopyCallback(callback, new File(to, file.getName()), file);
			}
		}
	}

	// TODO: Optimize for resource files. We can maybe just call onResourceMerge
	// which will merge with an empty resource file.
	private void doCopyCallback(RecipeDocumentCallback callback, File to, File from) {
		callback.onCopy(from, to);
	}
	
	private void handleInstantiate(RecipeDocumentCallback callback, String fromPath, String toPath) {
		File to = toPath(toPath, fromPath);
		String from = fromPath(fromPath);
		freemarker.template.Template template = converter.get(config, from);

		if ( isResource(from) || isJava(from) ) {
			List<String> result = converter.asListOfStrings(template, properties);
			callback.onInstantiate(result, to);
		} else {
			throw new UnsupportedOperationException("merging unknown type: " + fromPath);
		}
	}

	private void handleMerge(RecipeDocumentCallback callback, String fromPath, String toPath) {
		File to = toPath(toPath, fromPath);
		String from = fromPath(fromPath);
		freemarker.template.Template template = converter.get(config, from);
		
		if ( isManifest(from) ) {
			Document result = converter.asDocument(template, properties);
			callback.onManifestMerge(result, to);
		} else if ( isResource(from) ) {
			List<String> result = converter.asListOfStrings(template, properties);
			callback.onResourceMerge(result, to);
		} else {
//			List<String> result = converter.asListOfStrings(template, properties);
//			callback.onMerge(result, toFile);
			throw new UnsupportedOperationException("merging unknown type: " + fromPath);
		}
	}

	private boolean isJava(String from) {
		return from.contains(".java");
	}
	
	private boolean isResource(String from) {
		return from.startsWith("root/res/");
	}

	private boolean isManifest(String from) {
		return from.contains("AndroidManifest.xml");
	}

	private File toPath( String toPath, String fromPath ) {
		if ( toPath != null ) {
			return new File( toPath );
		} else if ( fromPath.endsWith(".ftl") ) {
			String cleanedFilename = fromPath.substring(0, fromPath.indexOf(".ftl"));
			return new File( cleanedFilename );
		} else {
			return new File( fromPath );	
		}
	}
	
	private String fromPath(String path) {
		String separator = path.startsWith("/") ? "" : File.separator;
		return Joiner.on(separator).join("root", path);
	}
}
