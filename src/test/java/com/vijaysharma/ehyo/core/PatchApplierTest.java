package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vijaysharma.ehyo.core.PatchApplier.DiffPrinter;
import com.vijaysharma.ehyo.core.PatchApplier.FileWriter;
import com.vijaysharma.ehyo.core.models.AsListOfStrings;
import com.vijaysharma.ehyo.core.models.HasDocument;

import difflib.Patch;

public class PatchApplierTest {
	private Function<HasDocument, String> renderer;
	private PatchApplier<HasDocument, AsListOfStrings> patcher;
	private FileWriter writer;
	private DiffPrinter printer;
	
	@Before
	public void before() {
		renderer = mock(Function.class);
		writer = mock(FileWriter.class);
		printer = mock(DiffPrinter.class);
		
		patcher = new PatchApplier<HasDocument, AsListOfStrings>(writer, printer, renderer);
	}
	
	@Test
	public void apply_returns_with_no_diff() {
		HasDocument document = mock(HasDocument.class);
		AsListOfStrings originalObject = mock(AsListOfStrings.class);
		AsListOfStrings modifiedObject = mock(AsListOfStrings.class);
		List<String> original = Lists.newArrayList();
		List<String> modified = Lists.newArrayList();
		
		when(document.asDocument()).thenReturn(originalObject);
		when(originalObject.toListOfStrings()).thenReturn(original);
		when(modifiedObject.toListOfStrings()).thenReturn(modified);
		
		Map<HasDocument, AsListOfStrings> files = Maps.newHashMap();
		files.put(document, modifiedObject);
		
		patcher.apply(files, Mockito.anyBoolean());
		verify(renderer, never()).apply(Mockito.any(HasDocument.class));
		verify(writer, never()).write(Mockito.any(HasDocument.class), Mockito.anyList());
		verify(printer, never()).print(Mockito.any(Patch.class));
	}
}
