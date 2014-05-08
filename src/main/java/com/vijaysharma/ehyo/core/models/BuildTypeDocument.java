package com.vijaysharma.ehyo.core.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/*
Property name	 		Default values for debug	 	Default values for release / other
debuggable	 			true	 						false
jniDebugBuild	 		false	 						false
renderscriptDebugBuild	false	 						false
renderscriptOptimLevel	3	 							3
packageNameSuffix	 	null	 						null
versionNameSuffix	 	null	 						null
signingConfig	 		android.signingConfigs.debug	null
zipAlign	 			false	 						true
runProguard	 			false	 						false
proguardFile	 		N/A (set only)	 				N/A (set only)
proguardFiles	 		N/A (set only)	 				N/A (set only)
*/

public class BuildTypeDocument {
	public static BuildTypeDocument read(String buildName, Collection<String> properties) {
		Map<String, String> props = Maps.newHashMap();
		return new BuildTypeDocument(buildName, props);
	}

	private final String name;
	private final Map<String, String> properties;
	
	public BuildTypeDocument(String name, Map<String, String> properties) {
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
