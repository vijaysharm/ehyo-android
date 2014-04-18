package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.models.GradleBuildDocumentModel.build;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class GradleBuildDocumentModelTest {

	@Test
	public void build_only_returns_contexts_with_properties() {
		List<String> lines = Lists.newArrayList(
			"apply plugin: 'android'",
			"android {","}",
			"dependencies {","}"
		);
		
		Multimap<String, String> result = build(lines);
		assertEquals(1, result.keySet().size());
		
		Collection<String> properties = result.get("root");
		assertEquals(1, properties.size());
		
		assertTrue(properties.contains("apply plugin: 'android'"));
	}
	
	@Test
	public void build_only_returns_contexts_with_properties_even_with_deep_nested() {
		List<String> lines = Lists.newArrayList(
			"apply plugin: 'android'",
			"android {",
			"	flavor1 {",
			"	}",
			"}",
			"",
			"dependencies {","}"
		);
		
		Multimap<String, String> result = build(lines);
		assertEquals(1, result.keySet().size());
		
		Collection<String> properties = result.get("root");
		assertEquals(1, properties.size());
		
		assertTrue(properties.contains("apply plugin: 'android'"));
	}
	
	@Test
	public void build_ignores_empty_lines() {
		List<String> lines = Lists.newArrayList(
			"apply plugin: 'android'",
			"android {","}",
			"",
			"dependencies {","}"
		);
		
		Multimap<String, String> result = build(lines);
		assertEquals(1, result.keySet().size());
		
		Collection<String> properties = result.get("root");
		assertEquals(1, properties.size());
		
		assertTrue(properties.contains("apply plugin: 'android'"));
	}
	
	@Test
	public void build_reads_all_contexts() {
		List<String> lines = Lists.newArrayList(
			"apply plugin: 'android'",
			"android {",
			"	compileSdkVersion 19",
			"}",
			"",
			"dependencies {",
			"    compile 'com.squareup.picasso:picasso:2.1.1'",
			"}"
		);
		
		Multimap<String, String> result = build(lines);
		assertEquals(3, result.keySet().size());
		
		Collection<String> properties = result.get("root");
		assertEquals(1, properties.size());
		assertTrue(properties.contains("apply plugin: 'android'"));
		
		properties = result.get("root.android");
		assertEquals(1, properties.size());
		assertTrue(properties.contains("compileSdkVersion 19"));
		
		properties = result.get("root.dependencies");
		assertEquals(1, properties.size());
		assertTrue(properties.contains("compile 'com.squareup.picasso:picasso:2.1.1'"));
	}
	
	@Test
	public void build_() {
		List<String> lines = Lists.newArrayList(
			"android {",
			"	productFlavors {",
			"		flavor1 {",
			"			versionCode 20",
			"		}",
			"	}",
			"}"
		);
		
		GradleBuildDocumentModel model = new GradleBuildDocumentModel(lines);
		Set<String> keys = model.getKeysStartingWith("root.android.productFlavors.");
		assertEquals(1, keys.size());
		assertTrue(keys.contains("root.android.productFlavors.flavor1"));
	}
}
