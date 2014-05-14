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

import difflib.ChangeDelta;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.InsertDelta;
import difflib.Patch;

public class PatchApplier<T extends HasDocument, K extends AsListOfStrings> {
	private final Function<T, K> factory;
	private final FileWriter writer;
	private final TextOutput out;
	
	public PatchApplier(Function<T, K> factory) {
		this(new FileWriter(), factory, Output.out);
	}
	
	PatchApplier(FileWriter writer, Function<T, K> producer, TextOutput out) {
		this.factory = producer;
		this.writer = writer;
		this.out = out;
	}
	
	public void apply(Map<T, K> files, boolean dryrun) {
		for ( Map.Entry<T, K> file : files.entrySet() ) {
			try {
				List<String> baseline = toListOfStrings(factory.apply(file.getKey()));
				List<String> changed = toListOfStrings(file.getValue());
				Patch diff = DiffUtils.diff(baseline, changed);
				if ( diff.getDeltas().isEmpty() ) {
					return;
				}
				
				if ( dryrun ) {
					show(file.getKey(), baseline, diff);
				} else {
					save(file.getKey(), file.getValue());
				}					
			} catch (Exception ex) {
				// TODO: What to do here? Should I roll back the changes? At the
				// very least, I should print there was an exception
			}
		}
	}
	
	private static <T extends AsListOfStrings> List<String> toListOfStrings(T document) {
		return document.toListOfStrings();
	}
	
	private void show(T item, List<String> baseline, Patch diff) throws IOException {
		StringBuilder output = new StringBuilder();
		output.append("Diff " + item.getFile() + "\n");
		for (Delta delta: diff.getDeltas()) {
			printDelta(baseline, output, delta);
		}
		
		out.print(output.toString());
	}

	private void save(T item, K modified) throws IOException {
		out.print("Writing " + item.getFile() + "... ");
		List<String> changed = toListOfStrings(modified);
		writer.write(item, changed);
		out.println("done");
	}
	
	static class FileWriter {
		public void write(HasDocument document, List<String> lines) {
			EFileUtil.write(document.getFile(), lines);			
		}
	}
	
	private static void printDelta(List<String> baseline, StringBuilder out, Delta delta) {
		if ( delta instanceof ChangeDelta ) {
			printChange(delta, baseline, out);
		} else if ( delta instanceof InsertDelta ) {
			printInsert(delta, baseline, out);
		} else if ( delta instanceof DeleteDelta ) {
			printDelete(delta, baseline, out);	
		}
		out.append("\n");
	}

	private static void printDelete(Delta delta, List<String> baseline, StringBuilder out) {
		int position = delta.getOriginal().getPosition();
		
		if (position > 0)
			out.append((position-1) + "  "  + baseline.get((position-1)) + "\n");
		
		List<?> lines = delta.getOriginal().getLines();
		for ( int index = 0; index < lines.size(); index++ ) {
			out.append((position++) + " -"  + lines.get(index) + "\n");
		}
		
		if ( position < baseline.size() )
			out.append((position) + "  "  + baseline.get((position)) + "\n");		
	}
	
	private static void printInsert(Delta delta, List<String> baseline, StringBuilder out) {
		int position = delta.getOriginal().getPosition();
		
		if (position > 0)
			out.append( (position-1) + "  "  + baseline.get((position-1)) + "\n");
		
		List<?> lines = delta.getRevised().getLines();
		for ( int index = 0; index < lines.size(); index++ ) {
			out.append((position++) + " +"  + lines.get(index) + "\n");
		}
		
		if ( position < baseline.size() )
			out.append((position) + "  "  + baseline.get((delta.getOriginal().getPosition())) + "\n");
	}

	private static void printChange(Delta delta, List<String> baseline, StringBuilder out) {
		int position = delta.getOriginal().getPosition();
		
		if (position > 0)
			out.append( (position-1) + "  "  + baseline.get((position-1)) + "\n");

		List<?> lines = delta.getOriginal().getLines();
		for ( int index = 0; index < lines.size(); index++ ) {
			out.append((position++) + " -"  + lines.get(index) + "\n");
		}
		
		position = delta.getRevised().getPosition();
		lines = delta.getRevised().getLines();
		for ( int index = 0; index < lines.size(); index++ ) {
			out.append((position++) + " +"  + lines.get(index) + "\n");
		}
		
		if ( position < baseline.size() )
			out.append((position) + "  "  + baseline.get((position)) + "\n");		
	}
	
}
