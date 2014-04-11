package com.vijaysharma.ehyo.core.commandline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vijaysharma.ehyo.core.commandline.CommandLineParser.ParsedSet;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInActionsTest {

	@Captor
	ArgumentCaptor<ArgumentOption<String>> captor;
	
	@Test
	public void configure_sets_version_as_option() {
		BuiltInActions action = new BuiltInActions();
		CommandLineParser parser = mock(CommandLineParser.class);
		action.configure(parser);
		
		verify(parser, times(1)).addOptions(captor.capture());
		ArgumentOption<String> value = captor.getValue();
		assertEquals(true, value.supports("--version"));
	}
	
	@Test
	public void getAction_returns_null_if_option_is_not_present() {
		BuiltInActions action = new BuiltInActions();
		ParsedSet options = mock(ParsedSet.class);
		when(options.has(Mockito.any(ArgumentOption.class))).thenReturn(false);
		assertNull(action.getAction(options));
	}
	
	@Test
	public void getAction_returns_an_action_if_option_is_present() {
		BuiltInActions action = new BuiltInActions();
		ParsedSet options = mock(ParsedSet.class);
		when(options.has(Mockito.any(ArgumentOption.class))).thenReturn(true);
		assertNotNull(action.getAction(options));
	}
}
