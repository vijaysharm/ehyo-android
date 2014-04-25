package com.vijaysharma.ehyo.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.PatchApplier.FileWriter;
import com.vijaysharma.ehyo.core.models.AsListOfStrings;
import com.vijaysharma.ehyo.core.models.HasDocument;

public class PatchApplierTest {
	private Function<HasDocument, AsListOfStrings> factory;
	private PatchApplier<HasDocument, AsListOfStrings> patcher;
	private FileWriter writer;
	private TextOutput out;
	
	@Before
	public void before() {
		factory = mock(Function.class);
		writer = mock(FileWriter.class);
		out = mock(TextOutput.class);
		
		patcher = new PatchApplier<HasDocument, AsListOfStrings>(writer, factory, out);
	}
	
	@Test
	public void apply_returns_with_no_diff_with_dryrun_false() {
		HasDocument document = mock(HasDocument.class);
		AsListOfStrings originalObject = mock(AsListOfStrings.class);
		AsListOfStrings modifiedObject = mock(AsListOfStrings.class);
		List<String> original = Lists.newArrayList();
		List<String> modified = Lists.newArrayList();
		
		when(factory.apply(document)).thenReturn(originalObject);
		when(originalObject.toListOfStrings()).thenReturn(original);
		when(modifiedObject.toListOfStrings()).thenReturn(modified);
		
		Map<HasDocument, AsListOfStrings> files = Maps.newHashMap();
		files.put(document, modifiedObject);
		
		patcher.apply(files, false);
		verify(factory, times(1)).apply(any(HasDocument.class));
		verify(writer, never()).write(any(HasDocument.class), Mockito.anyList());
		verify(out, never()).println(Mockito.anyString());
	}
	
	@Test
	public void apply_returns_with_no_diff_with_dryrun_true() {
		HasDocument document = mock(HasDocument.class);
		AsListOfStrings originalObject = mock(AsListOfStrings.class);
		AsListOfStrings modifiedObject = mock(AsListOfStrings.class);
		List<String> original = Lists.newArrayList();
		List<String> modified = Lists.newArrayList();
		
		when(factory.apply(document)).thenReturn(originalObject);
		when(originalObject.toListOfStrings()).thenReturn(original);
		when(modifiedObject.toListOfStrings()).thenReturn(modified);
		
		Map<HasDocument, AsListOfStrings> files = Maps.newHashMap();
		files.put(document, modifiedObject);
		
		patcher.apply(files, true);
		verify(factory, times(1)).apply(any(HasDocument.class));
		verify(writer, never()).write(any(HasDocument.class), Mockito.anyList());
		verify(out, never()).println(Mockito.anyString());
	}
	
	@Test
	public void apply_prints_when_dryrun() {
		HasDocument document = mock(HasDocument.class);
		AsListOfStrings originalObject = mock(AsListOfStrings.class);
		AsListOfStrings modifiedObject = mock(AsListOfStrings.class);
		List<String> original = Lists.newArrayList();
		List<String> modified = Lists.newArrayList("New Line");
		
		when(factory.apply(document)).thenReturn(originalObject);
		when(originalObject.toListOfStrings()).thenReturn(original);
		when(modifiedObject.toListOfStrings()).thenReturn(modified);
		when(document.toString()).thenReturn("test");
		
		Map<HasDocument, AsListOfStrings> files = Maps.newHashMap();
		files.put(document, modifiedObject);
		
		patcher.apply(files, true);
		verify(factory, times(1)).apply(document);
		verify(writer, never()).write(any(HasDocument.class), Mockito.anyList());
		verify(out, times(1)).print(
				"Diff test\n"+ 
				"0 +New Line\n\n" );
	}
	
	@Test
	public void apply_writes_when_dryrun_is_false() {
		HasDocument document = mock(HasDocument.class);
		AsListOfStrings originalObject = mock(AsListOfStrings.class);
		AsListOfStrings modifiedObject = mock(AsListOfStrings.class);
		List<String> original = Lists.newArrayList();
		List<String> modified = Lists.newArrayList("New Line");
		
		when(factory.apply(document)).thenReturn(originalObject);
		when(originalObject.toListOfStrings()).thenReturn(original);
		when(modifiedObject.toListOfStrings()).thenReturn(modified);
		when(document.toString()).thenReturn("test");
		
		Map<HasDocument, AsListOfStrings> files = Maps.newHashMap();
		files.put(document, modifiedObject);
		
		patcher.apply(files, false);
		verify(factory, times(1)).apply(document);
		verify(writer, times(1)).write(document, modified);
		
		verify(out, times(1)).print("Writing test... ");
		verify(out, times(1)).println("done");
	}	
}
