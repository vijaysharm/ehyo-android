package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.models.HasDocument;
import com.vijaysharma.ehyo.core.models.ResourceDocument;

public class ResourceChangeManager implements ChangeManager<PluginActions> {
	private static final Function<DocumentWrapper, ResourceDocument> FACTORY = new Function<DocumentWrapper, ResourceDocument>() {
		@Override
		public ResourceDocument apply(DocumentWrapper input) {
			return ResourceDocument.read(input.getFile());
		}
	};
	
	static class ResourceChangeManagerFactory {
		public ResourceChangeManager create() {
			return new ResourceChangeManager(new PatchApplier<DocumentWrapper, ResourceDocument>(FACTORY));
		}
	}
	
	private final ImmutableMap.Builder<DocumentWrapper, ResourceDocument> changes;
	private final Function<DocumentWrapper, ResourceDocument> factory;
	private final PatchApplier<DocumentWrapper, ResourceDocument> patcher;
	
	private ResourceChangeManager(PatchApplier<DocumentWrapper, ResourceDocument> patcher) {
		this(patcher, FACTORY);
	}
	
	ResourceChangeManager(PatchApplier<DocumentWrapper, ResourceDocument> patcher, Function<DocumentWrapper, ResourceDocument> factory) {
		this.factory = factory;
		this.changes = ImmutableMap.builder();
		this.patcher = new PatchApplier<DocumentWrapper, ResourceDocument>(factory);
	}

	@Override
	public void apply(PluginActions actions) {
		Set<File> files = actions.getResourceFiles();
		
		for ( File file : files ) {
			DocumentWrapper key = new DocumentWrapper(file);
			changes.put(key, factory.apply(key));
		}
		
		ResourceActionHandler handler = new ResourceActionHandler();
		for ( Entry<DocumentWrapper, ResourceDocument> entry : changes.build().entrySet() ) {
			handler.modify(entry.getValue(), actions.getResourceActions(entry.getKey().getFile()));
		}
	}

	@Override
	public void commit(boolean dryrun) {
		patcher.apply(changes.build(), dryrun);
	}
	
	private static class DocumentWrapper implements HasDocument {
		private final File file;

		public DocumentWrapper(File file) {
			this.file = file;
		}
		
		@Override
		public File getFile() {
			return file;
		}
		
		@Override
		public String toString() {
			return file.toString();
		}
	}
}
