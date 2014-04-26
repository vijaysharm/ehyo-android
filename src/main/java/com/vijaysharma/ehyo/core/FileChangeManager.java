package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.FileDocument;

public class FileChangeManager implements ChangeManager<PluginActions> {
	private static final Function<File, FileDocument> FACTORY = new Function<File, FileDocument>() {
		@Override
		public FileDocument apply(File input) {
			return new FileDocument(input);
		}
	};
	
	private final ImmutableMap.Builder<File, FileDocument> changes;
	private final Function<File, FileDocument> factory;
	
	public FileChangeManager() {
		this(FACTORY);
	}
	
	FileChangeManager(Function<File, FileDocument> factory) {
		this.factory = factory;
		this.changes = ImmutableMap.builder();
	}

	@Override
	public void apply(PluginActions actions) {
		Set<File> files = Sets.newHashSet(actions.getCreatedFiles().keySet());
		
		for ( File file : files ) {
			changes.put(file, factory.apply(file));
		}
		
		
	}

	@Override
	public void commit(boolean dryrun) {
		
	}
}
