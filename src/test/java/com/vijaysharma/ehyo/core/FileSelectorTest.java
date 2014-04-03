package com.vijaysharma.ehyo.core;

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

public class FileSelectorTest {

	@Test
	public void select_returns_empty_list_with_invalid_string_selection() {
		Function<String, String> renderer = Functions.identity();
		FileSelector<String> selectors = new FileSelector<String>(renderer, string("string"));
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(0, selection.size());
	}
	
	@Test
	public void select_returns_empty_list_with_invalid_int_selection() {
		Function<String, String> renderer = Functions.identity();
		FileSelector<String> selectors = new FileSelector<String>(renderer, string("99"));
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(0, selection.size());
	}
	
	@Test
	public void select_returns_item_when_selection_is_valid() {
		Function<String, String> renderer = Functions.identity();
		FileSelector<String> selectors = new FileSelector<String>(renderer, string("1"));
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(1, selection.size());
		assertEquals("one", selection.get(0));
	}
	
	@Test
	public void select_returns_array_when_selection_is_the_last_one() {
		Function<String, String> renderer = Functions.identity();
		FileSelector<String> selectors = new FileSelector<String>(renderer, string("3"));
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(2, selection.size());
		assertEquals("one", selection.get(0));
		assertEquals("two", selection.get(1));
	}
	
	private static final ByteArrayInputStream string(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}
}
