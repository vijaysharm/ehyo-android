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

// TODO: Should not filter out empty context
//		 This can be useful if you want to add to a section
//		 that doesn't exist, or is empty for some reason
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
	
	public void addTo(String path, String toAdd) {
		int startOf = getStartOf(path);
		if ( startOf != -1 )
			this.lines.add(startOf + 1, toAdd);
	}
	
	public boolean removeFrom(String path, String toRemove) {
		int index = getLine(path, toRemove);
		if ( index == -1 ) {
			return false;
		}
		
		this.lines.remove(index);
		return true;
	}

	public List<String> getLines() {
		return lines;
	}
	
	private int getLine(String path, String property) {
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
				if ( line.equals(property) && path(context).equals(path)) {
					return index;
				}
			}
		}
		
		return -1;
	}
	
	private int getStartOf(String path) {
		LinkedList<String> context = Lists.newLinkedList();
		context.add("root");
		for ( int index = 0; index < lines.size(); index++ ) {
			String line = lines.get(index).trim();

			if ( line.length() == 0 )
				continue;

			if ( line.endsWith("{") ) {
				String name = line.substring(0, line.indexOf("{")).trim();
				context.offer(name);
				
				if (path(context).equals(path))
					return index;
			} else if ( line.endsWith("}")) {
				context.removeLast();
			} else {
				// skip properties
			}
		}
		
		return -1;
	}
	
	private static String path(LinkedList<String> paths) {
		return Joiner.on(".").join(paths);
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
				build.put(path(context), line);
			}
		}
		
		return build.build();
	}
}
