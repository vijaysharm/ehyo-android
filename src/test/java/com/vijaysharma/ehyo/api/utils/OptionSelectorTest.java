package com.vijaysharma.ehyo.api.utils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.TextOutput;

public class OptionSelectorTest {
	private TextOutput out;
	
	@Before
	public void before() {
		out = mock(TextOutput.class);
	}
	
	@Test
	public void select_returns_empty_list_with_invalid_string_selection() {
		Function<String, String> renderer = Functions.identity();
		OptionSelector<String> selectors = new OptionSelector<String>("", renderer, string("string"), out);
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(0, selection.size());
	}
	
	@Test
	public void select_returns_empty_list_with_invalid_int_selection() {
		Function<String, String> renderer = Functions.identity();
		OptionSelector<String> selectors = new OptionSelector<String>("", renderer, string("99"), out);
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(0, selection.size());
	}
	
	@Test
	public void select_returns_item_when_selection_is_valid() {
		Function<String, String> renderer = Functions.identity();
		OptionSelector<String> selectors = new OptionSelector<String>("", renderer, string("1"), out);
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(1, selection.size());
		assertEquals("one", selection.get(0));
	}
	
	@Test
	public void select_returns_array_when_selection_is_the_last_one() {
		Function<String, String> renderer = Functions.identity();
		OptionSelector<String> selectors = new OptionSelector<String>("", renderer, string("3"), out);
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list);
		assertEquals(2, selection.size());
		assertEquals("one", selection.get(0));
		assertEquals("two", selection.get(1));
	}
	
	@Test
	public void select_returns_empty_array_when_selection_is_the_last_one_but_multiselect_is_false() {
		Function<String, String> renderer = Functions.identity();
		OptionSelector<String> selectors = new OptionSelector<String>("", renderer, string("3"), out);
		
		List<String> list = Lists.newArrayList("one", "two");
		List<String> selection = selectors.select(list, false);
		assertEquals(0, selection.size());
	}
	
	private static final ByteArrayInputStream string(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}
}
