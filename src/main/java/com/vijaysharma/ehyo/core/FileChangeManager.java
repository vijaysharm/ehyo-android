package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.models.FileDocument;
import com.vijaysharma.ehyo.core.models.HasDocument;

public class FileChangeManager implements ChangeManager<PluginActions> {
	private static final Function<FileWrapper, FileDocument> FACTORY = new Function<FileWrapper, FileDocument>() {
		@Override
		public FileDocument apply(FileWrapper input) {
			return FileDocument.read(input.getFile());
		}
	};
	
	static class FileChangeManagerFactory {
		public FileChangeManager create() {
			return new FileChangeManager(new PatchApplier<FileWrapper, FileDocument>(FACTORY));
		}
	}
	
	private final ImmutableMap.Builder<FileWrapper, FileDocument> changes;
	private final Function<FileWrapper, FileDocument> factory;
	private final PatchApplier<FileWrapper, FileDocument> patcher;
	
	private FileChangeManager(PatchApplier<FileWrapper, FileDocument> patcher) {
		this(patcher, FACTORY);
	}
	
	FileChangeManager(PatchApplier<FileWrapper, FileDocument> patcher, Function<FileWrapper, FileDocument> factory) {
		this.factory = factory;
		this.changes = ImmutableMap.builder();
		this.patcher = new PatchApplier<FileWrapper, FileDocument>(factory);
	}

	@Override
	public void apply(PluginActions actions) {
		Set<File> files = actions.getFiles();
		
		for ( File file : files ) {
			FileWrapper key = new FileWrapper(file);
			changes.put(key, factory.apply(key));
		}
		
		FileActionHandler handler = new FileActionHandler();
		for ( Entry<FileWrapper, FileDocument> entry : changes.build().entrySet() ) {
			handler.modify(entry.getValue(), actions.getFileActions(entry.getKey().getFile()));
		}
	}

	@Override
	public void commit(boolean dryrun) {
		patcher.apply(changes.build(), dryrun);
	}
	
	private static class FileWrapper implements HasDocument {
		private final File file;

		public FileWrapper(File file) {
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
