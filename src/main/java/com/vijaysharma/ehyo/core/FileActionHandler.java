package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.List;

import com.vijaysharma.ehyo.core.InternalActions.FileActions;
import com.vijaysharma.ehyo.core.models.FileDocument;

public class FileActionHandler implements PluginActionHandler<FileDocument, FileActions> {
	@Override
	public void modify(FileDocument item, FileActions actions) {
		createNewFiles(item, actions);
	}

	private void createNewFiles(FileDocument item, FileActions actions) {
		Collection<List<String>> files = actions.getCreatedFiles();
		for ( List<String> lines : files ) {
			item.addAll(lines);
		}
	}
}
