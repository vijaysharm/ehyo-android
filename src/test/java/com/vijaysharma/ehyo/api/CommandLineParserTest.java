package com.vijaysharma.ehyo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;

public class CommandLineParserTest {
	private ArgumentOption<String> op1;
	
	@Before
	public void before() {
		op1 = mock(ArgumentOption.class);
	}
	
	@Test
	public void parse_finds_passed_ArgumentOption() {
		when(op1.supports("--help")).thenReturn(true);
		List<ArgumentOption<?>> op = ops(op1);
		
		List<String> args = Lists.newArrayList("--help");
		CommandLineParser parser = new CommandLineParser(op);
		ParsedSet optionSet = parser.parse(args);
		assertTrue(optionSet.has(op1));
		assertTrue(optionSet.getRemainingArgs().isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void value_throws_on_seen_ArgumentOption_expecting_argument() {
		when(op1.supports("--help")).thenReturn(true);
		when(op1.getDefaultArgValue()).thenReturn("default");
		List<ArgumentOption<?>> op = ops(op1);
		
		List<String> args = Lists.newArrayList("--help");
		CommandLineParser parser = new CommandLineParser(op);
		ParsedSet optionSet = parser.parse(args);
		assertTrue(optionSet.has(op1));
		assertTrue(optionSet.getRemainingArgs().isEmpty());
		optionSet.value(op1);
	}
	
	@Test
	public void parse_doesnt_finds_passed_ArgumentOption() {
		when(op1.supports("--help")).thenReturn(false);
		List<ArgumentOption<?>> op = ops(op1);
		
		List<String> args = Lists.newArrayList("--help");
		CommandLineParser parser = new CommandLineParser(op);
		ParsedSet optionSet = parser.parse(args);
		assertFalse(optionSet.has(op1));
		assertFalse(optionSet.getRemainingArgs().isEmpty());
		assertEquals(1, optionSet.getRemainingArgs().size());
		assertEquals("--help", optionSet.getRemainingArgs().get(0));
	}
	
	@Test(expected=UsageException.class)
	public void parse_throws_when_cannot_find_argument_for_ArgumentOption() {
		when(op1.supports("--add")).thenReturn(true);
		when(op1.hasRequiredArg()).thenReturn(true);
		List<ArgumentOption<?>> op = ops(op1);
		
		List<String> args = Lists.newArrayList("--add");
		CommandLineParser parser = new CommandLineParser(op);
		parser.parse(args);
	}
	
	@Test
	public void parse_finds_argument_for_ArgumentOption() {
		when(op1.supports("--add")).thenReturn(true);
		when(op1.hasRequiredArg()).thenReturn(true);
		when(op1.getRequiredArgType()).thenReturn(String.class);
		when(op1.getDefaultArgValue()).thenReturn("default");

		List<ArgumentOption<?>> op = ops(op1);

		List<String> args = Lists.newArrayList("--add", "doc");
		CommandLineParser parser = new CommandLineParser(op);
		ParsedSet optionSet = parser.parse(args);
		assertTrue(optionSet.has(op1));
		assertEquals("doc", optionSet.value(op1));
		assertTrue(optionSet.getRemainingArgs().isEmpty());
	}
	
	private static List<ArgumentOption<?>> ops(ArgumentOption<?>...options) {
		List<ArgumentOption<?>> ops = Lists.newArrayList();
		for( ArgumentOption<?> op : options )
			ops.add(op);
		
		return ops;
	}
}
