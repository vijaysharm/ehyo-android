package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

public class BinaryFileChangeManager implements ChangeManager<PluginActions> {
	static class BinaryFileChangeManagerFactory {
		public BinaryFileChangeManager create() {
			return new BinaryFileChangeManager();
		}
	}
	
	private final ImmutableMap.Builder<File, File> changes;
	private final TextOutput out;
	
	private BinaryFileChangeManager() {
		this.changes = ImmutableMap.builder();
		out = Output.out;
	}
	
	@Override
	public void apply(PluginActions actions) {
		changes.putAll(actions.getBinaryFiles());
	}

	@Override
	public void commit(boolean dryrun) {
		for ( Entry<File, File> file : changes.build().entrySet() ) {
			File from = file.getKey();
			File to = file.getValue();
			if (dryrun) {
				out.println("Copying from: [" + from + "] to: [" + to + "]");
			}
			else {
				out.println("Creating " + to);
				EFileUtil.copyFile(from, to);
			}
		}
	}
}
