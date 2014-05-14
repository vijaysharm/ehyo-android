package com.vijaysharma.ehyo.core.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/*
Property Name	 			Default value in DSL object	 	Default value
versionCode	 				-1	 							value from manifest if present
versionName					null	 						value from manifest if present
minSdkVersion	 			-1	 							value from manifest if present
targetSdkVersion			-1	 							value from manifest if present
packageName	 				null	 						value from manifest if present
testPackageName	 			null	 						app package name + “.test”
testInstrumentationRunner	null	 						android.test.InstrumentationTestRunner
signingConfig	 			null	 						null
proguardFile	 			N/A (set only)	 				N/A (set only)
proguardFiles	 			N/A (set only)	 				N/A (set only) 
*/
public class FlavorDocument {
	public static FlavorDocument read(String flavorName, Collection<String> properties) {
		Map<String, String> props = Maps.newHashMap();
		return new FlavorDocument(flavorName, props);
	}
	
	private final String name;
	private final Map<String, String> properties;
	
	public FlavorDocument(String flavorName, Map<String, String> props) {
		this.name = flavorName;
		this.properties = props;
	}
	
	public String getName() {
		return name;
	}
	
	String getProperty(String id) {
		return properties.get(id);
	}
}
