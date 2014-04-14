package com.vijaysharma.ehyo.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.models.AsListOfStrings;
import com.vijaysharma.ehyo.core.models.HasDocument;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class PatchApplier<T extends HasDocument, K extends AsListOfStrings> {
	private final Function<T, String> renderer;
	private final FileWriter writer;
	private final DiffPrinter diffPrinter;
	private final TextOutput out;
	
	public PatchApplier(Function<T, String> renderer) {
		this(new FileWriter(), new DiffPrinter(), renderer, Output.out);
	}
	
	PatchApplier(FileWriter writer, DiffPrinter printer, Function<T, String> renderer, TextOutput out) {
		this.renderer = renderer;
		this.writer = writer;
		this.diffPrinter = printer;
		this.out = out;
	}
	
	public void apply(Map<T, K> files, boolean dryrun) {
		for ( Map.Entry<T, K> file : files.entrySet() ) {
			try {
				List<String> baseline = toListOfStrings(file.getKey().asDocument());
				List<String> changed = toListOfStrings(file.getValue());
				Patch diff = DiffUtils.diff(baseline, changed);
				if ( diff.getDeltas().isEmpty() ) {
					return;
				}
				
				if ( dryrun ) {
					show(file.getKey(), diff);
				} else {
					save(file.getKey(), file.getValue());
				}					
			} catch (Exception ex) {
				// TODO: What to do here? Should I roll back the changes?
				// TODO: At the very least, I should print there was an exception
			}
		}
	}
	
	private static <T extends AsListOfStrings> List<String> toListOfStrings(T document) {
		return document.toListOfStrings();
	}
	
	private void show(T item, Patch diff) throws IOException {		
		out.println("Diff " + renderer.apply(item));
		diffPrinter.print(diff);
	}

	private void save(T item, K modified) throws IOException {
		out.print("Writing " + renderer.apply(item) + "... ");
		List<String> changed = toListOfStrings(modified);
		writer.write(item, changed);
		out.println("done");
	}
	
	static class DiffPrinter {
		public void print(Patch patch) {
			for (Delta delta: patch.getDeltas()) {
				Output.out.println(delta);
			}				
		}
	}
	
	static class FileWriter {
		public void write(HasDocument document, List<String> lines) {
			EFileUtil.write(document.getFile(), lines);			
		}
	}
}
