package com.vijaysharma.ehyo.core.models;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;


public class GradleBuildDocumentModel {
	private final List<String> lines;
	
	public GradleBuildDocumentModel(List<String> lines) {
		this.lines = lines;
	}
	
	
	public Collection<String> getProperties(String path) {
		Multimap<String, String> build = build(this.lines);
		return build.get(path);
	}
	
	public Set<String> getKeysStartingWith( String key ) {
		Multimap<String, String> build = build(this.lines);
		ImmutableSet.Builder<String> keys = ImmutableSet.builder();
		for ( String context : build.keySet() ) {
			if ( context.startsWith(key) )
				keys.add(context);
		}
		return keys.build();
	}
	
	static Multimap<String, String> build(List<String> lines) {
		ImmutableMultimap.Builder<String, String> build = ImmutableMultimap.builder();
		
		LinkedList<String> context = Lists.newLinkedList();
		context.add("root");

		for ( int index = 0; index < lines.size(); index++ ) {
			String line = lines.get(index).trim();

			if ( line.length() == 0 )
				continue;

			if ( line.endsWith("{") ) {
				String name = line.substring(0, line.indexOf("{")).trim();
				context.offer(name);
			} else if ( line.endsWith("}")) {
				context.removeLast();
			} else {
				
				build.put(Joiner.on(".").join(context.iterator()), line);
			}
		}
		
		return build.build();
	}
}
