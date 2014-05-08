package com.vijaysharma.ehyo.core.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/*
"sourceSets.jnidebug.setRoot('foo/jnidebug')",
"manifest.srcFile 'AndroidManifest.xml'
"java.srcDirs = ['src']
"resources.srcDirs = ['src']
"aidl.srcDirs = ['src']
"renderscript.srcDirs = ['src']
"res.srcDirs = ['res']
"assets.srcDirs = ['assets']
setRoot('tests')
java.srcDirs = ['src/java'],
 */
public class SourceSetDocument {
	public static SourceSetDocument read(String name, Collection<String> properties) {
		Map<String, String> props = Maps.newHashMap();
		return new SourceSetDocument(name, props);
	}
	
	private final String name;
	private final Map<String, String> properties;
	
	public SourceSetDocument(String name, Map<String, String> properties) {
		this.name = name;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
}
